/*
 * Copyright 2019-2023 the original author or authors.
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
package com.mammb.code.editor2.model.glyph;

/**
 * Font.
 * @param name the font name
 * @param size the size of font
 * @param weight the weight of font(normal:400 bold:700)
 * @param posture the posture of font
 * @author Naotsugu Kobayashi
 */
public record Font(String name, double size, int weight, String posture) { }
