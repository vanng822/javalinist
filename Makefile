ROOT := $(shell pwd)
OS := $(shell uname)
tag := $(shell cat VERSION)
name := javalinist
repo_name := javalinist
image_tag := $(name):$(tag)

ifeq ($(OS),Darwin)
DOCKER := docker
TAG_VERSION := openjdk
DOCKER_PORT_FORWARD_ARGS := -p 127.0.0.1:8080:8080
ifeq ($(REPOS_PATH),)
$(error error is "REPOS_PATH not set")
endif
ifeq ($(DOCKER_REMOTE_HOST),)
$(error error is "DOCKER_REMOTE_HOST not set")
endif
ifeq ("$(wildcard .git/hooks/pre-push)","")
$(error error is ".git/hooks/pre-push not set, run 'ln -s ./script/pre-push.sh .git/hooks/pre-push'")
endif
else
DOCKER := sudo docker
TAG_VERSION := openjdk:8-jre
DOCKER_PORT_FORWARD_ARGS :=
endif

.PHONY: build
build: build-jar
	$(DOCKER) build --build-arg TAG_VERSION=$(TAG_VERSION) -t $(image_tag) .

test:
	./gradlew test --no-daemon

run:
	$(DOCKER) run --restart=always --network raspberrypi3_default \
	 	--ip 172.18.0.9 $(DOCKER_PORT_FORWARD_ARGS) --name $(name) -d -it $(image_tag)

stop:
	$(DOCKER) stop $(name)

rm:
	$(DOCKER) rm $(name)

deploy: build
	make stop
	make rm
	make run

tail: CONTAINER_ID=$(shell $(DOCKER) ps -f name=$(name) -q)
tail:
	$(DOCKER) logs -f $(CONTAINER_ID)

pull:
	ssh -C $(DOCKER_REMOTE_HOST) "cd $(REPOS_PATH)/$(repo_name) && git pull --rebase"

release:
	ssh -C $(DOCKER_REMOTE_HOST) "cd $(REPOS_PATH)/$(repo_name) && git pull --rebase && make deploy"

.PHONY: build-jar
build-jar:
	./gradlew build --no-daemon

bash: CONTAINER_ID=$(shell $(DOCKER) ps -f name=$(name) -q)
bash:
	$(DOCKER) exec -it $(CONTAINER_ID) bash

remote-tail:
	ssh -C $(DOCKER_REMOTE_HOST) "cd $(REPOS_PATH)/$(repo_name) && make tail"