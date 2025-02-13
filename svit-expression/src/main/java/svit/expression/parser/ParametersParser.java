package svit.expression.parser;

import svit.expression.ast.ParameterNode;
import svit.ast.lexer.Lexer;
import svit.ast.node.Node;
import svit.ast.parser.Parser;
import svit.ast.parser.ParserContext;
import svit.expression.ast.ParametersNode;

import static svit.ast.token.DefaultToken.*;

public class ParametersParser implements Parser {

    @Override
    public void parse(Lexer lexer, Node parent, ParserContext context) {
        ParametersNode parameters = new ParametersNode();

        shift(lexer, T_OPEN_BRACE);

        do {
            ensureNext(lexer, T_IDENTIFIER);

            ParameterNode parameter = new ParameterNode();
            // parser identifier and shift to equal symbol 'identifier='
            Node identifier = context.getParser(IdentifierParser.class).parse(lexer, context);
            parameter.setKey(identifier);
            shift(lexer, T_EQ);
            parameter.setValue(context.getParser(AnyExpressionParser.class).parse(lexer, context));

            // soft move
            lexer.moveNext(T_COMMA);

            parameters.addParameter(parameter);

        } while (lexer.isCurrent(T_COMMA));

        shift(lexer, T_CLOSE_BRACE);

        parent.add(parameters);
    }

}
