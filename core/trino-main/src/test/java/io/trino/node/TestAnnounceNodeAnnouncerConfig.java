/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.node;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static io.airlift.configuration.testing.ConfigAssertions.assertFullMapping;
import static io.airlift.configuration.testing.ConfigAssertions.assertRecordedDefaults;
import static io.airlift.configuration.testing.ConfigAssertions.recordDefaults;

class TestAnnounceNodeAnnouncerConfig
{
    @Test
    void testDefaults()
    {
        assertRecordedDefaults(recordDefaults(AnnounceNodeAnnouncerConfig.class)
                .setCoordinatorUris(List.of()));
    }

    @Test
    void testExplicitPropertyMappings()
    {
        Map<String, String> properties = ImmutableMap.<String, String>builder()
                .put("discovery.uri", "https://example.com:100,https://example.org:200")
                .buildOrThrow();

        AnnounceNodeAnnouncerConfig expected = new AnnounceNodeAnnouncerConfig()
                .setCoordinatorUris(List.of(URI.create("https://example.com:100"), URI.create("https://example.org:200")));

        assertFullMapping(properties, expected);
    }
}
