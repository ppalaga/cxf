/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.staxutils;

import java.util.Objects;

import javax.xml.namespace.QName;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamReader;

public class StaxStreamFilter implements StreamFilter {

    private QName[] tags;

    /**
     * @param eventsToReject
     * @deprecated use {@link #excludeElements(QName...)} or {@link #excludeElement(QName)} instead.
     */
    @Deprecated
    public StaxStreamFilter(QName... eventsToReject) {
        tags = eventsToReject;
    }

    /**
     * Returns a new {@link StreamFilter} that removes all elements matching the given {@code excludedElements}
     * {@link QName}s. Consider using {@link #excludeElement(QName)} of you pass just a single {@link QName}.
     *
     * @param excludedElements the {@link QName}s to filter out
     * @return a new {@link StreamFilter}
     */
    public static StreamFilter excludeElements(QName... excludedElements) {
        if (excludedElements.length == 1) {
            return new SingleElementStreamFilter(excludedElements[0]);
        } else {
            return new StaxStreamFilter(excludedElements);
        }
    }

    /**
     * Returns a new {@link StreamFilter} that removes all elements matching the given {@code excludedElement}
     * {@link QName}.
     *
     * @param excludedElement the {@link QName} to filter out
     * @return a new {@link StreamFilter}
     */
    public static StreamFilter excludeElement(QName excludeElement) {
        return new SingleElementStreamFilter(excludeElement);
    }

    public boolean accept(XMLStreamReader reader) {
        if (reader.isStartElement()) {
            QName elName = reader.getName();
            for (QName tag : tags) {
                if (elName.equals(tag)) {
                    return false;
                }
            }
        } else if (reader.isEndElement()) {
            QName elName = reader.getName();
            for (QName tag : tags) {
                if (elName.equals(tag)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * An implementation optimized for excluding elements matching a single {@link QName}.
     */
    private static final class SingleElementStreamFilter implements StreamFilter {
        private final QName excludedElement;

        private SingleElementStreamFilter(QName excludedElement) {
            super();
            this.excludedElement = Objects.requireNonNull(excludedElement);
        }

        @Override
        public boolean accept(XMLStreamReader reader) {
            return (!reader.isStartElement() && !reader.isEndElement())
                    || !excludedElement.equals(reader.getName());
        }

    }
}
