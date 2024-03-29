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
package com.mammb.code.editor.model.text;

/**
 * Translate.
 * @param <I> the type of input
 * @param <O> the type of output
 * @author Naotsugu Kobayashi
 */
public interface Translate<I, O> {

    /**
     * Apply to translate.
     * @param input the input element
     * @return the output element
     */
    O applyTo(I input);


    /**
     * Get the contextual or not.
     * @return {@code true} if this translate is contextual
     */
    default boolean contextual() {
        return false;
    }


    /**
     * This Translate is combined with the other Translate specified in the argument.
     * @param that the other Translate
     * @return the combined Translate
     * @param <X> the translated type
     */
    default <X> Translate<I, X> compound(Translate<O, X> that) {
        if (contextual() || that.contextual()) {
            return new Translate<>() {
                @Override
                public X applyTo(I in) {
                    return that.applyTo(Translate.this.applyTo(in));
                }
                @Override
                public boolean contextual() {
                    return true;
                }
            };
        } else {
            return in -> that.applyTo(applyTo(in));
        }
    }

}
