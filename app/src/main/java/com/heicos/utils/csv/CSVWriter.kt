package com.heicos.utils.csv

import java.io.IOException
import java.io.PrintWriter
import java.io.Writer

class CSVWriter(
    writer: Writer,
    private val separator: Char = DEFAULT_SEPARATOR,
    private val quote: Char = DEFAULT_QUOTE_CHARACTER,
    private val escape: Char = DEFAULT_ESCAPE_CHARACTER,
    private val lineEnd: String = DEFAULT_LINE_END
) {

    private val printWriter: PrintWriter = PrintWriter(writer)

    fun writeNext(nextLine: Array<String?>?) {
        if (nextLine == null)
            return

        val sb = StringBuffer()
        for (i in nextLine.indices) {

            if (i != 0) {
                sb.append(separator)
            }

            val nextElement = nextLine[i] ?: continue
            if (quote != NO_QUOTE_CHARACTER)
                sb.append(quote)
            for (element in nextElement) {
                if (escape != NO_ESCAPE_CHARACTER && element == quote) {
                    sb.append(escape).append(element)
                } else if (escape != NO_ESCAPE_CHARACTER && element == escape) {
                    sb.append(escape).append(element)
                } else {
                    sb.append(element)
                }
            }
            if (quote != NO_QUOTE_CHARACTER)
                sb.append(quote)
        }

        sb.append(lineEnd)
        printWriter.write(sb.toString())
    }

    @Throws(IOException::class)
    fun close() {
        printWriter.flush()
        printWriter.close()
    }

    companion object {
        const val DEFAULT_ESCAPE_CHARACTER = '"'
        const val DEFAULT_SEPARATOR = ','
        const val DEFAULT_QUOTE_CHARACTER = '"'
        const val NO_QUOTE_CHARACTER = '\u0000'
        const val NO_ESCAPE_CHARACTER = '\u0000'
        const val DEFAULT_LINE_END = "\n"
    }

}