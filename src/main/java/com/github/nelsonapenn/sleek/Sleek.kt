package com.github.nelsonapenn.sleek

//import com.google.gson.Gson

class Sleek(source: String? = null, jsonNode: JsonNode? = null){
    private companion object
    {
        private val matchTable: List<Pair<Regex, TokenType>> = listOf(
                Pair<Regex, TokenType>(Regex("""\{"""), TokenType.LBRACE),
                Pair<Regex, TokenType>(Regex("""}"""), TokenType.RBRACE),
                Pair<Regex, TokenType>(Regex("""\["""), TokenType.LBRACKET),
                Pair<Regex, TokenType>(Regex("""]"""), TokenType.RBRACKET),
                Pair<Regex, TokenType>(Regex(""","""), TokenType.COMMA),
                Pair<Regex, TokenType>(Regex(""":"""), TokenType.COLON),
                Pair<Regex, TokenType>(Regex("""null"""), TokenType.NULL),
                Pair<Regex, TokenType>(Regex("""(true|false)"""), TokenType.BOOLEAN),
                Pair<Regex, TokenType>(Regex("""-?([1-9][0-9]*|0)(\.[0-9]+)?([Ee][+\-]?[0-9]+)?"""), TokenType.NUMBER),
                Pair<Regex, TokenType>(Regex(""""([^"\\]|\\(?<escapetype>"|\\|/|b|f|n|r|t|u(?<codepoint>(?:[0-9]|[a-f]|[A-F]){4})))*""""), TokenType.STRING),
                Pair<Regex, TokenType>(Regex("""."""), TokenType.INVALID)
        )

        fun lex(source: String): List<Token>
        {
            var offset = 0;
            val out = MutableList<Token>(0){_ -> Token(TokenType.INVALID, "")};
            val ws = Regex("""[ \n\r\t]*""")

            if(offset < source.length)
            {
                val wsMatch = ws.find(source, offset)
                if(wsMatch?.range?.first == offset)
                {
                    offset += wsMatch.value.length
                }
            }


            while (offset < source.length)
            {
                var currentToken = Token();

                for( (regex, tokenType) in matchTable)
                {
                    val match = regex.find(source, offset)
                    if(match?.range?.first == offset)
                    {
                        when(tokenType)
                        {
                            TokenType.NUMBER -> {
                                currentToken = JsonNumberToken(match.value)
                            }
                            TokenType.STRING ->
                            {
                                currentToken = JsonStringToken(match.value)
                            }
                            TokenType.BOOLEAN ->
                            {
                                currentToken = JsonBooleanToken(match.value)
                            }
                            else -> {
                                currentToken.rawString = match.value
                                currentToken.tokenType = tokenType
                            }
                        }
                        offset += match.value.length
                        break
                    }

                }

                if(currentToken.tokenType == TokenType.INVALID)
                    throw Error("Invalid JSON provided")
                out.add(currentToken);

                val wsMatch = ws.find(source, offset)
                if(wsMatch?.range?.first == offset)
                {
                    offset += wsMatch.value.length
                }
            }

            return out
        }
    }
    private val tokens:MutableList<Token>
    private val jsonNode: JsonNode
    init
    {
        if(source != null)
        {
            tokens = lex(source) as MutableList<Token>
            this.jsonNode = readNode()
        }
        else if(jsonNode != null)
        {
            tokens = mutableListOf()
            this.jsonNode = jsonNode
        }
        else
        {
            tokens = mutableListOf()
            this.jsonNode = JsonNode()
        }
    }

    operator fun get(i: String): Sleek
    {
        val cast = jsonNode as? JsonObject ?: throw(AintNoJsonObjectException())
        val value = cast.value[i] ?: throw(JsonKeyException())
        return Sleek(jsonNode = value)
    }
    operator fun get(i: Int): Sleek
    {
        val cast = jsonNode as? JsonArray ?: throw(AintNoJsonArrayException())
        if(i < 0 || i >= cast.value.size)
            throw(JsonArrayIndexOutOfBoundsException())

        val value = cast.value[i]
        return Sleek(jsonNode = value)
    }


    val map: Map<String, Sleek>
    get()
    {
        val out = mutableMapOf<String, Sleek>()
        val map = (jsonNode as? JsonObject)?.value ?: throw AintNoJsonObjectException()
        for( (key, value) in map)
        {
            out[key] = Sleek(jsonNode = value)
        }
        return out
    }

    val floatMap: Map<String, Float?>
        get()
        {
            val out = mutableMapOf<String, Float?>()
            val map = (jsonNode as? JsonObject)?.value ?: throw AintNoJsonObjectException()
            for( (key, value) in map)
            {
                out[key] = Sleek(jsonNode = value).float
            }
            return out
        }
    val intMap: Map<String, Int?>
        get()
        {
            val out = mutableMapOf<String, Int?>()
            val map = (jsonNode as? JsonObject)?.value ?: throw AintNoJsonObjectException()
            for( (key, value) in map)
            {
                out[key] = Sleek(jsonNode = value).int
            }
            return out
        }
    val stringMap: Map<String, String?>
        get()
        {
            val out = mutableMapOf<String, String?>()
            val map = (jsonNode as? JsonObject)?.value ?: throw AintNoJsonObjectException()
            for( (key, value) in map)
            {
                out[key] = Sleek(jsonNode = value).string
            }
            return out
        }
    val booleanMap: Map<String, Boolean?>
        get()
        {
            val out = mutableMapOf<String, Boolean?>()
            val map = (jsonNode as? JsonObject)?.value ?: throw AintNoJsonObjectException()
            for( (key, value) in map)
            {
                out[key] = Sleek(jsonNode = value).boolean
            }
            return out
        }

    val list: List<Sleek>
    get()
    {
        val out = mutableListOf<Sleek>()
        val list = (jsonNode as? JsonArray)?.value ?: throw AintNoJsonArrayException()
        for( value in list)
        {
            out.add( Sleek(jsonNode = value) )
        }
        return out
    }

    val floatList: List<Float?>
        get()
        {
            val out = mutableListOf<Float?>()
            val list = (jsonNode as? JsonArray)?.value ?: throw AintNoJsonArrayException()
            for( value in list)
            {
                out.add( Sleek(jsonNode = value).float )
            }
            return out
        }
    val intList: List<Int?>
        get()
        {
            val out = mutableListOf<Int?>()
            val list = (jsonNode as? JsonArray)?.value ?: throw AintNoJsonArrayException()
            for( value in list)
            {
                out.add( Sleek(jsonNode = value).int )
            }
            return out
        }
    val stringList: List<String?>
        get()
        {
            val out = mutableListOf<String?>()
            val list = (jsonNode as? JsonArray)?.value ?: throw AintNoJsonArrayException()
            for( value in list)
            {
                out.add( Sleek(jsonNode = value).string )
            }
            return out
        }
    val booleanList: List<Boolean?>
        get()
        {
            val out = mutableListOf<Boolean?>()
            val list = (jsonNode as? JsonArray)?.value ?: throw AintNoJsonArrayException()
            for( value in list)
            {
                out.add( Sleek(jsonNode = value).boolean )
            }
            return out
        }

    val string: String?
        get()
        {
            val token = (jsonNode as? JsonLiteral)?.value
            when(token?.tokenType)
            {
                TokenType.STRING ->
                {
                    return (token as? JsonStringToken)?.value ?: throw ThisShouldntBeHappeningError()
                }
                TokenType.NULL ->
                {
                    return null
                }
                else ->
                {
                    throw AintNoJsonStringLiteralException()
                }

            }
        }

    val boolean: Boolean?
        get()
        {
            val token = (jsonNode as? JsonLiteral)?.value
            when(token?.tokenType)
            {
                TokenType.BOOLEAN ->
                {
                    return (token as? JsonBooleanToken)?.value ?: throw ThisShouldntBeHappeningError()
                }
                TokenType.NULL ->
                {
                    return null
                }
                else ->
                {
                    throw AintNoJsonBooleanLiteralException()
                }

            }
        }

    val float: Float?
        get()
        {
            val token = (jsonNode as? JsonLiteral)?.value
            when(token?.tokenType)
            {
                TokenType.NUMBER ->
                {
                    return (token as? JsonNumberToken)?.value ?: throw ThisShouldntBeHappeningError()
                }
                TokenType.NULL ->
                {
                    return null
                }
                else ->
                {
                    throw AintNoJsonNumberLiteralException()
                }

            }
        }

    val int: Int?
        get()
        {
            return float?.toInt()
        }


    private fun consume(expectedType: TokenType): Token
    {
        if(tokens.isEmpty())
            throw(AintValidJsonException("Unexpected end of input."))

        val found = tokens[0]
        if(found.tokenType != expectedType)
            throw(AintValidJsonException("Expected ${expectedType.name}, but found ${found.tokenType.name} instead."))
        tokens.removeAt(0)
        return found
    }

    private val currentToken:Token
        get()
        {
            if(tokens.isEmpty())
                throw(AintValidJsonException("Unexpected end of input."))
            return tokens[0]
        }

    private fun discard(expectedType: TokenType)
    {
        consume(expectedType)
    }

    private fun readNode(): JsonNode
    {
        return when(currentToken.tokenType)
        {
            TokenType.LBRACE -> {
                readObject()
            }
            TokenType.LBRACKET ->
            {
                readArray()
            }
            else ->
            {
                readLiteral()
            }
        }
    }

    private fun readObject(): JsonObject
    {
        val out = JsonObject()
        discard(TokenType.LBRACE)

        // if empty
        if(currentToken.tokenType == TokenType.RBRACE)
        {
            discard(TokenType.RBRACE)
            return out
        }

        // read first
        val firstKey = consume(TokenType.STRING)
        discard(TokenType.COLON)
        val firstValue = readNode()

        out.value[(firstKey as JsonStringToken).value] = firstValue

        // read rest
        while(currentToken.tokenType != TokenType.RBRACE)
        {
            discard(TokenType.COMMA)
            val key = consume(TokenType.STRING)
            discard(TokenType.COLON)
            val value = readNode()
            out.value[(key as JsonStringToken).value] = value
        }
        discard(TokenType.RBRACE)

        return out
    }

    private fun readArray(): JsonArray
    {
        val out = JsonArray()
        discard(TokenType.LBRACKET)

        // if empty
        if(currentToken.tokenType == TokenType.RBRACKET)
        {
            discard(TokenType.RBRACKET)
            return out
        }

        // read first
        val firstValue = readNode()
        out.value.add(firstValue)

        // read rest
        while(currentToken.tokenType != TokenType.RBRACKET)
        {
            discard(TokenType.COMMA)
            val value = readNode()
            out.value.add(value)
        }
        discard(TokenType.RBRACKET)

        return out
    }

    private fun readLiteral(): JsonLiteral
    {
        val out = JsonLiteral()
        val okayList = listOf<TokenType>(TokenType.NUMBER, TokenType.STRING, TokenType.BOOLEAN, TokenType.NULL)
        when(currentToken.tokenType)
        {
            in okayList -> {
                out.value = consume(currentToken.tokenType)
                return out
            }
            else -> {
                throw AintValidJsonException("Not valid JSON literal.")
            }
        }
    }

}

enum class TokenType
{
    NUMBER,
    STRING,
    BOOLEAN,
    LBRACE,
    RBRACE,
    LBRACKET,
    RBRACKET,
    COLON,
    COMMA,
    NULL,
    INVALID
}

open class Token(var tokenType: TokenType = TokenType.INVALID, var rawString: String = "")

class JsonNumberToken(rawString: String) : Token(TokenType.NUMBER, rawString)
{
    val value: Float
        get()
        {
            return rawString.toFloatOrNull() ?: throw ThisShouldntBeHappeningError()
        }
}

class JsonStringToken(rawString: String) : Token(TokenType.STRING, rawString)
{
    val value: String
        get()
        {
            var outString = ""
            assert(rawString[0] == '"' && rawString[rawString.length - 1] == '"')
            val escape = Regex("""\\(?<escapetype>"|\\|/|b|f|n|r|t|u(?<codepoint>(?:[0-9]|[a-f]|[A-F]){4}))""")
            var offset = 1
            while(offset < rawString.length - 1)
            {
                val char = rawString[offset]
                if(char == '\\')
                {
                    val match = escape.find(rawString, offset)
                    if(match == null || match.range.first != offset)
                        throw AintValidJsonException("Invalid escape sequence present in JSON string.")
                    val (escapeType, codePoint) = match.destructured

                    outString += when(escapeType)
                    {
                        "\"" -> '\"'
                        "\\" -> '\\'
                        "/" -> '/'
                        "b" -> '\b'
                        "f" -> '\u000c'
                        "n" -> '\n'
                        "r" -> '\r'
                        "t" -> '\t'
                        else ->
                        {
                            val intCodePoint: Int = Integer.parseInt(codePoint, 16)
                            Character.toChars(intCodePoint)[0]
                        }
                    }
                    offset += match.value.length

                }
                else
                {
                    outString += char
                    offset += 1
                }
            }

            return outString
        }
}

class JsonBooleanToken(rawString: String) : Token(TokenType.BOOLEAN, rawString)
{
    val value: Boolean
        get()
        {
            if(rawString == "true")
                return true
            else if(rawString == "false")
                return false
            throw ThisShouldntBeHappeningError()
        }
}

open class JsonNode
{
}

class JsonObject: JsonNode()
{
    var value = mutableMapOf<String, JsonNode>()
}

class JsonArray: JsonNode()
{
    var value = mutableListOf<JsonNode>()
}

class JsonLiteral: JsonNode()
{
    var value = Token()
}

class AintValidJsonException(message:String):Exception(message)

class ThisShouldntBeHappeningError(): Exception("This shouldn't be happening! Nooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo")

class JsonKeyException():Exception("Key not present in JSON object.")
class JsonArrayIndexOutOfBoundsException():Exception("Array index out of bounds for JSON array.")

class AintNoJsonNumberLiteralException():Exception("Attempt to cast non-number JSON data to number.")
class AintNoJsonStringLiteralException():Exception("Attempt to cast non-string JSON data to string.")
class AintNoJsonBooleanLiteralException():Exception("Attempt to cast non-boolean JSON data to boolean.")
class AintNoJsonArrayException():Exception("Attempt to cast non-array JSON data to array.")
class AintNoJsonObjectException():Exception("Attempt to cast non-object JSON data to object.")
