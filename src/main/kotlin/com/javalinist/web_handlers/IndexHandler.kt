package com.javalinist.web_handlers

import com.javalinist.handlers.BaseHandler
import com.vladsch.flexmark.ast.AutoLink
import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.MutableDataSet
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.rendering.template.TemplateUtil
import com.vladsch.flexmark.profile.pegdown.Extensions
import com.vladsch.flexmark.util.misc.Extension
import java.io.File
import java.util.*


class IndexHandler: BaseHandler, Handler {

    override fun handle(ctx: Context) {
        val readme = File("./README.md")
        val options = MutableDataSet()
        options.set(Parser.EXTENSIONS, Collections.singleton(AutolinkExtension.create() as Extension))
        val parser: Parser = Parser.builder(options).build()
        val renderer = HtmlRenderer.builder(options).build()
        val document: Node = parser.parse(readme.readText())
        val html = renderer.render(document)
        ctx.render("/templates/index.html", TemplateUtil.model("readme", html))
    }
}