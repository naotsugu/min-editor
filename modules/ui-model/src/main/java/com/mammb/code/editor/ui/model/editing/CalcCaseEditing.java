/*
 * Copyright 2023-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mammb.code.editor.ui.model.editing;

import com.mammb.code.editor.ui.model.EditorModel;
import com.mammb.code.editor.ui.model.ModelQuery;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Calc case editing.
 * @author Naotsugu Kobayashi
 */
public class CalcCaseEditing implements Editing {

    /** logger. */
    private static final System.Logger log = System.getLogger(CalcCaseEditing.class.getName());

    @Override
    public boolean apply(EditorModel model, Queryable query) {
        String text = query.apply(ModelQuery.selectedText());
        if (text.isBlank()) {
            return false;
        }

        try {
            String s = text.contains(" ") ? " " : "";
            model.selectionReplace("%s%s=%s%s".formatted(text, s, s, calc(text)));
            return true;
        } catch (Exception e) {
            log.log(System.Logger.Level.WARNING, e);
            return false;
        }
    }


    /**
     * Calculate the given formula.
     * @param text the formula string
     * @return the result string
     */
    static String calc(String text) {
        List<String> rpn = infixToRpn(text);
        BigDecimal result = evaluate(rpn);
        return result.toPlainString();
    }


    /**
     * Convert the infix notation to the reverse polish notation.
     * @param text the input text
     * @return the reverse polish notation list
     */
    static List<String> infixToRpn(String text) {
        List<String> tokens = Arrays.stream(text.splitWithDelimiters("[^a-zA-Z_0-9\\.]", -1))
            .filter(Predicate.not(String::isBlank)).toList();
        return shuntingYard(tokens);
    }


    /**
     * Evaluate the reverse polish notation.
     * @param rpn the reverse polish notation
     * @return the result of evaluate
     */
    static BigDecimal evaluate(List<String> rpn) {
        if (rpn == null || rpn.isEmpty()) {
            return BigDecimal.ZERO;
        }
        Stack<String> stack = new Stack<>();
        for (String token : rpn) {
            stack.push(switch (token) {
                case "+" -> new BigDecimal(stack.pop()).add(new BigDecimal(stack.pop())).toPlainString();
                case "-" -> new BigDecimal(stack.pop()).subtract(new BigDecimal(stack.pop())).toPlainString();
                case "*" -> new BigDecimal(stack.pop()).multiply(new BigDecimal(stack.pop())).toPlainString();
                case "^" -> new BigDecimal(stack.pop()).pow(Integer.parseInt(stack.pop())).toPlainString();
                case "/" -> {
                    var divisor = new BigDecimal(stack.pop());
                    yield new BigDecimal(stack.pop()).divide(divisor).toPlainString();
                }
                default -> token;
            });
        }
        return new BigDecimal(stack.pop());
    }


    /**
     * The computational operators.
     */
    private enum Operator {
        ADD("+", false, 0),
        SUB("-", true,  0),
        DIV("/", false, 5),
        MUL("*", false, 5),
        POW("^", true, 10);

        final String symbol;
        final boolean rightAssociative;
        final int precedence;

        Operator(String symbol, boolean rightAssociative, int precedence) {
            this.symbol = symbol;
            this.rightAssociative = rightAssociative;
            this.precedence = precedence;
        }

        public boolean isRightAssociative() {
            return rightAssociative;
        }

        public boolean isLeftAssociative() {
            return !isRightAssociative();
        }

        public String symbol() {
            return symbol;
        }

        public int comparePrecedence(Operator operator) {
            return this.precedence - operator.precedence;
        }

        final static Map<String, Operator> values = Arrays.stream(Operator.values())
            .collect(Collectors.toMap(Operator::symbol, UnaryOperator.identity()));
    }


    /**
     * Get the reverse polish notation by shunting yard algorithm.
     * @param tokens the infix notation tokens
     * @return the the reverse polish notation token list
     */
    private static List<String> shuntingYard(List<String> tokens) {

        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (String token: tokens) {
            if (Operator.values.containsKey(token)) {
                while (!stack.isEmpty() && Operator.values.containsKey(stack.peek())) {
                    Operator cOp = Operator.values.get(token);
                    Operator lOp = Operator.values.get(stack.peek());
                    if ((cOp.isLeftAssociative() && cOp.comparePrecedence(lOp) <= 0) ||
                        (cOp.isRightAssociative() && cOp.comparePrecedence(lOp) < 0)) {
                        output.add(stack.pop());
                        continue;
                    }
                    break;
                }
                stack.push(token);
            } else if ("(".equals(token)) {
                stack.push(token);
            } else if (")".equals(token)) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                stack.pop();
            } else {
                output.add(token);
            }
        }

        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }

        return output;
    }

}
