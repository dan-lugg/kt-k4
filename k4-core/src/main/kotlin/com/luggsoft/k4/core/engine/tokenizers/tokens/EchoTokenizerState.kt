package com.luggsoft.k4.core.engine.tokenizers.tokens

import com.luggsoft.k4.core.Source
import com.luggsoft.k4.core.engine.DefaultLocation
import com.luggsoft.k4.core.engine.tokenizers.TokenizerSettings

class EchoTokenizerState(
    tokenizerSettings: TokenizerSettings
) : TokenizerStateBase(
    tokenizerSettings = tokenizerSettings
)
{
    @Throws(TokenizerStateException::class)
    override fun getNextToken(source: Source, startIndex: Int, tokenizerStateSetter: TokenizerStateSetter): Token
    {
        var index = startIndex
        val stringBuffer = StringBuffer()
        while (index < source.text.length)
        {
            stringBuffer.append(source.text[index++])
            index = this.tryConsumeString(source, stringBuffer, index)
            index = this.tryConsumeRawString(source, stringBuffer, index)
            if (stringBuffer.endsWith("#>"))
            {
                tokenizerStateSetter(TextTokenizerState(this.tokenizerSettings))
                return EchoToken(
                    echo = stringBuffer.removeSuffix("#>").toString(),
                    location = DefaultLocation(
                        name = source.name,
                        startIndex = startIndex,
                        untilIndex = index
                    )
                )
            }
        }
        throw this.getUnexpectedEOFTokenizerStateException(source, startIndex, index)
    }
}
