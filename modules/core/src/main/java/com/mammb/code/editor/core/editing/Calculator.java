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
package com.mammb.code.editor.core.editing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Calculator.
 * @author Naotsugu Kobayashi
 */
class Calculator {

    /**
     * Calculate the given formula.
     * @param text the formula string
     * @return the result string
     */
    static String calc(String text) {
        try {
            List<String> rpn = infixToRpn(text.replace(",", ""));
            BigDecimal result = evaluate(rpn);
            return result.toPlainString();
        } catch (Exception e) {
            return "NaN";
        }
    }

    /**
     * Convert the infix notation to the reverse polish notation.
     * @param text the input text
     * @return the reverse polish notation list
     */
    static List<String> infixToRpn(String text) {
        List<String> tokens = Arrays.stream(text.replace("e-", "EM").splitWithDelimiters("[^a-zA-Z_0-9\\.]", -1))
            .filter(Predicate.not(String::isBlank))
            .map(t -> t.contains("EM") ? t.replace("EM", "e-") : t)
            .toList();
        return shuntingYard(tokens);
    }

    /**
     * Evaluate the reverse polish notation.
     * @param rpn the reverse polish notation
     * @return the result of evaluating
     */
    static BigDecimal evaluate(List<String> rpn) {
        if (rpn == null || rpn.isEmpty()) {
            return BigDecimal.ZERO;
        }
        Stack<String> stack = new Stack<>();
        for (String token : rpn) {
            stack.push(switch (token) {
                case "+" -> add(stack.pop(), stack.pop());
                case "-" -> sub(stack.pop(), stack.pop());
                case "*" -> mul(stack.pop(), stack.pop());
                case "/" -> div(stack.pop(), stack.pop());
                case "^" -> pow(stack.pop(), stack.pop());
                case "~" -> new BigDecimal(stack.pop()).negate().toPlainString();
                default -> token;
            });
        }
        return new BigDecimal(stack.pop());
    }

    private static String add(String l, String r) {
        return new BigDecimal(r).add(new BigDecimal(l)).toPlainString();
    }

    private static String sub(String l, String r) {
        return new BigDecimal(r).subtract(new BigDecimal(l)).toPlainString();
    }

    private static String mul(String l, String r) {
        return new BigDecimal(r).multiply(new BigDecimal(l)).toPlainString();
    }

    private static String div(String l, String r) {
        var divisor = new BigDecimal(l);
        var dividend = new BigDecimal(r);
        System.out.println(divisor.scale());
        System.out.println(dividend.scale());
        return dividend.divide(divisor, Math.min(divisor.scale(), dividend.scale()) + 1, RoundingMode.HALF_UP)
            .stripTrailingZeros().toPlainString();
    }

    private static String pow(String l, String r) {
        return new BigDecimal(r).pow(Integer.parseInt(l)).toPlainString();
    }

    /**
     * The computational operators.
     */
    private enum Operator {
        ADD("+", false, 0),
        SUB("-", false, 0),
        DIV("/", false, 5),
        MUL("*", false, 5),
        POW("^", true, 10),
        NRG("~", true, 15), // unary operation (like -2 * 3)
        ;

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
     * @return the reverse polish notation token list
     */
    private static List<String> shuntingYard(List<String> tokens) {

        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        boolean unaryAllowed = true;
        for (String token: tokens) {

            if (token == null || token.isBlank()) continue;

            if (Objects.equals(token, "-") && unaryAllowed) {
                token = "~";
            } else if (Objects.equals(token, "+") && unaryAllowed) {
                continue;
            }
            unaryAllowed = false;

            if (Operator.values.containsKey(token)) {
                unaryAllowed = true;
                while (!stack.isEmpty() && Operator.values.containsKey(stack.peek())) {
                    Operator cOp = Operator.values.get(token);
                    Operator lOp = Operator.values.get(stack.peek());
                    if ((cOp.isLeftAssociative() && cOp.comparePrecedence(lOp) <= 0) ||
                        (cOp.comparePrecedence(lOp) < 0)) {
                        // pop o2 from the operator stack into the output queue
                        output.add(stack.pop());
                        continue;
                    }
                    break;
                }
                // push o1 onto the operator stack
                stack.push(token);
            } else if ("(".equals(token)) {
                unaryAllowed = true;
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
