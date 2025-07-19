/*
 * Copyright 2023-2025 the original author or authors.
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

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link MarkdownTables}.
 * @author Naotsugu Kobayashi
 */
class MarkdownTablesTest {

    @Test
    void fromHtml() {
        var html = """
            <h1>Table 1</h1>
            <table>
              <caption>
                Front-end web developer course 2021
              </caption>
              <thead>
                <tr>
                  <th scope="col">Person</th>
                  <th scope="col">Most interest in</th>
                  <th scope="col">Age</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <th scope="row">Chris</th>
                  <td>HTML tables</td>
                  <td>22</td>
                </tr>
                <tr>
                  <th scope="row">Dennis</th>
                  <td>Web accessibility</td>
                  <td>45</td>
                </tr>
                <tr>
                  <th scope="row">Sarah</th>
                  <td>JavaScript frameworks</td>
                  <td>29</td>
                </tr>
                <tr>
                  <th scope="row">Karen</th>
                  <td>Web performance</td>
                  <td>36</td>
                </tr>
              </tbody>
              <tfoot>
                <tr>
                  <th scope="row" colspan="2">Average age</th>
                  <td>33</td>
                </tr>
              </tfoot>
            </table>
            <p>xxx</p>
            """;

        var md = MarkdownTables.fromHtml(html);
        assertEquals("""
            <h1>Table 1</h1>
            Front-end web developer course 2021

            | Person | Most interest in | Age |
            | --- | --- | --- |
            | Chris | HTML tables | 22 |
            | Dennis | Web accessibility | 45 |
            | Sarah | JavaScript frameworks | 29 |
            | Karen | Web performance | 36 |
            | Average age | 33 |
            <p>xxx</p>""", md);

        assertEquals("""
            Table 1
            Front-end web developer course 2021

            | Person | Most interest in | Age |
            | --- | --- | --- |
            | Chris | HTML tables | 22 |
            | Dennis | Web accessibility | 45 |
            | Sarah | JavaScript frameworks | 29 |
            | Karen | Web performance | 36 |
            | Average age | 33 |
            xxx""", EditingFunctions.removeHtmlTags.apply(md));

    }

}
