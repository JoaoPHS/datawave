package datawave.query.tables.chained.iterators;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.IteratorSetting;
import org.apache.accumulo.core.client.ScannerBase;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.client.sample.SamplerConfiguration;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import datawave.query.config.SSDeepSimilarityQueryConfiguration;
import datawave.query.tables.ScannerFactory;
import datawave.util.ssdeep.ChunkSizeEncoding;
import datawave.util.ssdeep.IntegerEncoding;
import datawave.util.ssdeep.NGramTuple;

/**
 * Iterator that lazily calculates ranges. The amount of ranges to be calculated at a time is configurable. This approach avoids creating all the ranges at the
 * same time and storing them all in memory for the life of the query.
 */
public class LazyLoadingRangesIterator implements Iterator<Map.Entry<Key,Value>>, ScannerBase {
    private static final Logger log = LoggerFactory.getLogger(LazyLoadingRangesIterator.class);

    private final Iterator<NGramTuple> queryMapKeysIterator;
    private final BatchScanner scanner;
    private Iterator<Map.Entry<Key,Value>> scannerIterator;
    private final int numRangesPerScanner;

    final ChunkSizeEncoding chunkSizeEncoder = new ChunkSizeEncoding();
    final IntegerEncoding bucketEncoder;
    final int indexBuckets;

    public LazyLoadingRangesIterator(SSDeepSimilarityQueryConfiguration config, ScannerFactory scannerFactory) throws TableNotFoundException {
        queryMapKeysIterator = config.getState().getQueryMap().keys().iterator();
        bucketEncoder = new IntegerEncoding(config.getBucketEncodingBase(), config.getBucketEncodingLength());
        indexBuckets = config.getIndexBuckets();
        scanner = scannerFactory.newScanner(config.getTableName(), config.getAuthorizations(), config.getQueryThreads(), config.getQuery());
        numRangesPerScanner = config.getNumRangesPerScanner();
    }

    /**
     * Continue creating new iterators using a new calculated set of ranges until a next result can be found
     *
     * @return true if a result has been found, false if not
     */
    @Override
    public boolean hasNext() {
        while (scannerIterator == null || !scannerIterator.hasNext()) {
            if (queryMapKeysIterator.hasNext()) {
                scannerIterator = createIteratorWithNewRanges();
            } else {
                return false;
            }
        }

        return scannerIterator.hasNext();
    }

    /**
     * Continue creating new iterators using a new calculated set of ranges until a next result can be found
     *
     * @return the next item
     */
    @Override
    public Map.Entry<Key,Value> next() {
        while (scannerIterator == null || !scannerIterator.hasNext()) {
            scannerIterator = createIteratorWithNewRanges();
        }

        return scannerIterator.next();
    }

    /**
     * Process the query map keys to create ranges to scan in Accumulo. The number of ranges generated at a time is determined by {@code numRangesPerScanner}.
     * The number of ranges may go over numRangesPerScanner slightly due to processing all index buckets per NGramTuple
     *
     * @return an iterator based on the next set of calculated ranges
     */
    private Iterator<Map.Entry<Key,Value>> createIteratorWithNewRanges() {
        final Collection<Range> ranges = new HashSet<>();

        while (queryMapKeysIterator.hasNext() && ranges.size() <= numRangesPerScanner) {
            NGramTuple ct = queryMapKeysIterator.next();
            final String sizeAndChunk = chunkSizeEncoder.encode(ct.getChunkSize()) + ct.getChunk();
            for (int i = 0; i < indexBuckets; i++) {
                final String bucketedSizeAndChunk = bucketEncoder.encode(i) + sizeAndChunk;
                ranges.add(Range.exact(new Text(bucketedSizeAndChunk)));
            }
        }

        scanner.setRanges(ranges);

        log.debug("Lazy loaded {} ranges.", ranges.size());
        log.trace("Ranges are: {}", ranges);

        return scanner.stream().iterator();
    }

    public ScannerBase getScanner() {
        return scanner;
    }

    @Override
    public void addScanIterator(IteratorSetting iteratorSetting) {
        scanner.addScanIterator(iteratorSetting);
    }

    @Override
    public void removeScanIterator(String s) {
        scanner.removeScanIterator(s);
    }

    @Override
    public void updateScanIteratorOption(String s, String s1, String s2) {
        scanner.updateScanIteratorOption(s, s1, s2);
    }

    @Override
    public void fetchColumnFamily(Text text) {
        scanner.fetchColumnFamily(text);
    }

    @Override
    public void fetchColumn(Text text, Text text1) {
        scanner.fetchColumn(text, text1);
    }

    @Override
    public void fetchColumn(IteratorSetting.Column column) {
        scanner.fetchColumn(column);
    }

    @Override
    public void clearColumns() {
        scanner.clearColumns();
    }

    @Override
    public void clearScanIterators() {
        scannerIterator = null;
        scanner.clearScanIterators();
    }

    @Override
    public Iterator<Map.Entry<Key,Value>> iterator() {
        return this;
    }

    @Override
    public void setTimeout(long l, TimeUnit timeUnit) {
        scanner.setTimeout(l, timeUnit);
    }

    @Override
    public long getTimeout(TimeUnit timeUnit) {
        return scanner.getTimeout(timeUnit);
    }

    @Override
    public void close() {
        scannerIterator = null;
        scanner.close();
    }

    @Override
    public Authorizations getAuthorizations() {
        return scanner.getAuthorizations();
    }

    @Override
    public void setSamplerConfiguration(SamplerConfiguration samplerConfiguration) {
        scanner.setSamplerConfiguration(samplerConfiguration);
    }

    @Override
    public SamplerConfiguration getSamplerConfiguration() {
        return scanner.getSamplerConfiguration();
    }

    @Override
    public void clearSamplerConfiguration() {
        scanner.clearSamplerConfiguration();
    }

    @Override
    public void setBatchTimeout(long l, TimeUnit timeUnit) {
        scanner.setBatchTimeout(l, timeUnit);
    }

    @Override
    public long getBatchTimeout(TimeUnit timeUnit) {
        return scanner.getBatchTimeout(timeUnit);
    }

    @Override
    public void setClassLoaderContext(String s) {
        scanner.setClassLoaderContext(s);
    }

    @Override
    public void clearClassLoaderContext() {
        scanner.clearClassLoaderContext();
    }

    @Override
    public String getClassLoaderContext() {
        return scanner.getClassLoaderContext();
    }

    @Override
    public ConsistencyLevel getConsistencyLevel() {
        return scanner.getConsistencyLevel();
    }

    @Override
    public void setConsistencyLevel(ConsistencyLevel consistencyLevel) {
        scanner.setConsistencyLevel(consistencyLevel);
    }
}
