package com.dedalow;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.logging.Level;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import com.dedalow.utils.Utils;
import com.dedalow.report.Report;

public class Launcher {

	public static void main(String[] args) {

		try {
            SharedDependencies.init();
			ArrayList<String> testCases = Utils.getTestCasesSelected();

			LauncherDiscoveryRequest discoveryRequest = LauncherDiscoveryRequestBuilder.request()
					.selectors(testCases.stream().map(DiscoverySelectors::selectClass).collect(Collectors.toList()))
					.build();

			org.junit.platform.launcher.Launcher launcher = LauncherFactory.create();
			launcher.execute(discoveryRequest);

		} catch (Exception e) {
			Report.reportConsoleLogs(e.getMessage(), Level.SEVERE);
		}
	}

}