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
package io.trino.server.protocol.spooling;

import io.airlift.configuration.Config;
import io.airlift.configuration.ConfigDescription;
import io.airlift.configuration.ConfigSecuritySensitive;
import io.airlift.units.DataSize;
import io.airlift.units.Duration;
import io.trino.util.Ciphers;
import jakarta.validation.constraints.AssertTrue;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.util.Optional;

import static io.airlift.units.DataSize.Unit.KILOBYTE;
import static io.airlift.units.DataSize.Unit.MEGABYTE;
import static io.trino.server.protocol.spooling.SpoolingConfig.SegmentRetrievalMode.COORDINATOR_STORAGE_REDIRECT;
import static java.util.Base64.getDecoder;

public class SpoolingConfig
{
    private Optional<SecretKey> sharedSecretKey = Optional.empty();
    private SegmentRetrievalMode retrievalMode = SegmentRetrievalMode.STORAGE;
    private Optional<Duration> storageRedirectTtl = Optional.empty();

    private boolean allowInlining = true;
    private long maximumInlinedRows = 1000;
    private DataSize maximumInlinedSize = DataSize.of(128, KILOBYTE);
    private DataSize initialSegmentSize = DataSize.of(8, MEGABYTE);
    private DataSize maximumSegmentSize = DataSize.of(16, MEGABYTE);

    public Optional<SecretKey> getSharedSecretKey()
    {
        return sharedSecretKey;
    }

    @ConfigDescription("256 bit, base64-encoded secret key used to secure segment identifiers")
    @Config("protocol.spooling.shared-secret-key")
    @ConfigSecuritySensitive
    public SpoolingConfig setSharedSecretKey(String sharedEncryptionKey)
    {
        this.sharedSecretKey = Optional.ofNullable(sharedEncryptionKey)
                .map(value -> new SecretKeySpec(getDecoder().decode(value), "AES"));
        return this;
    }

    public SegmentRetrievalMode getRetrievalMode()
    {
        return retrievalMode;
    }

    @Config("protocol.spooling.retrieval-mode")
    @ConfigDescription("Determines how the client will retrieve the segment")
    public SpoolingConfig setRetrievalMode(SegmentRetrievalMode retrievalMode)
    {
        this.retrievalMode = retrievalMode;
        return this;
    }

    public Optional<Duration> getStorageRedirectTtl()
    {
        return storageRedirectTtl;
    }

    @Config("protocol.spooling.coordinator-storage-redirect-ttl")
    @ConfigDescription("Determines how long the pre-signed URI generated by the coordinator allows for retrieval of data")
    public SpoolingConfig setStorageRedirectTtl(Optional<Duration> storageRedirectTtl)
    {
        this.storageRedirectTtl = storageRedirectTtl;
        return this;
    }

    public DataSize getInitialSegmentSize()
    {
        return initialSegmentSize;
    }

    @Config("protocol.spooling.initial-segment-size")
    @ConfigDescription("Initial size of the spooled segments in bytes")
    public SpoolingConfig setInitialSegmentSize(DataSize initialSegmentSize)
    {
        this.initialSegmentSize = initialSegmentSize;
        return this;
    }

    public DataSize getMaximumSegmentSize()
    {
        return maximumSegmentSize;
    }

    @Config("protocol.spooling.maximum-segment-size")
    @ConfigDescription("Maximum size of the spooled segments in bytes")
    public SpoolingConfig setMaximumSegmentSize(DataSize maximumSegmentSize)
    {
        this.maximumSegmentSize = maximumSegmentSize;
        return this;
    }

    public boolean isAllowInlining()
    {
        return allowInlining;
    }

    @ConfigDescription("Allow spooled protocol to inline data")
    @Config("protocol.spooling.inlining.enabled")
    public SpoolingConfig setAllowInlining(boolean allowInlining)
    {
        this.allowInlining = allowInlining;
        return this;
    }

    public long getMaximumInlinedRows()
    {
        return maximumInlinedRows;
    }

    @Config("protocol.spooling.inlining.max-rows")
    @ConfigDescription("Maximum number of rows that are allowed to be inlined per worker")
    public SpoolingConfig setMaximumInlinedRows(long maximumInlinedRows)
    {
        this.maximumInlinedRows = maximumInlinedRows;
        return this;
    }

    public DataSize getMaximumInlinedSize()
    {
        return maximumInlinedSize;
    }

    @Config("protocol.spooling.inlining.max-size")
    @ConfigDescription("Maximum size of rows that are allowed to be inlined per worker")
    public SpoolingConfig setMaximumInlinedSize(DataSize maximumInlinedSize)
    {
        this.maximumInlinedSize = maximumInlinedSize;
        return this;
    }

    @AssertTrue(message = "protocol.spooling.shared-secret-key must be 256 bits long")
    public boolean isSharedEncryptionKeyAes256()
    {
        return sharedSecretKey
                .map(Ciphers::is256BitSecretKeySpec)
                .orElse(true);
    }

    @AssertTrue(message = "protocol.spooling.shared-secret-key must be set")
    public boolean isSharedEncryptionKeySet()
    {
        return sharedSecretKey.isPresent();
    }

    @AssertTrue(message = "protocol.spooling.coordinator-storage-redirect-ttl can be set when protocol.spooling.retrieval-mode is COORDINATOR_STORAGE_REDIRECT")
    public boolean isStorageRedirectTtlCorrect()
    {
        if (retrievalMode == COORDINATOR_STORAGE_REDIRECT) {
            return true;
        }

        return storageRedirectTtl.isEmpty();
    }

    public enum SegmentRetrievalMode
    {
        // Client goes for the data to:
        STORAGE, // directly to the storage with the pre-signed URI (1 round trip)
        COORDINATOR_STORAGE_REDIRECT, // coordinator and gets redirected to the storage with the pre-signed URI (2 round trips)
        COORDINATOR_PROXY, // coordinator and gets segment data through it (1 round trip)
        WORKER_PROXY, // coordinator and gets redirected to one of the available workers and gets data through it (2 round trips)
    }
}
