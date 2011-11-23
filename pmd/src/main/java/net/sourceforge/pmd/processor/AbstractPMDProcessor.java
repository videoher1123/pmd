/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.processor;

import java.io.IOException;
import java.util.List;

import net.sourceforge.pmd.Configuration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;
import net.sourceforge.pmd.renderers.Renderer;

/**
 * @author Romain Pelisse <belaran@gmail.com>
 *
 */
public abstract class AbstractPMDProcessor {

	protected final Configuration configuration;
	
	public AbstractPMDProcessor(final Configuration configuration) {
		this.configuration = configuration;
	}

	public void renderReports(final List<Renderer> renderers, final Report report) {
		try {
			long start = System.nanoTime();
			for (Renderer r : renderers) {
				r.renderFileReport(report);
			}
			long end = System.nanoTime();
			Benchmarker.mark(Benchmark.Reporting, end - start, 1);
		} catch (IOException ioe) {
			
		}
	}
}
