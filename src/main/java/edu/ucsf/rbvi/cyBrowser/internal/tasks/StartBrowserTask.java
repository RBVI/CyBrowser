package edu.ucsf.rbvi.cyBrowser.internal.tasks;

import java.util.Properties;
import javax.swing.SwingUtilities;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;


import edu.ucsf.rbvi.cyBrowser.internal.model.CyBrowser;
import edu.ucsf.rbvi.cyBrowser.internal.model.CyBrowserManager;
import edu.ucsf.rbvi.cyBrowser.internal.view.ResultsPanelBrowser;
import edu.ucsf.rbvi.cyBrowser.internal.view.SwingBrowser;

public class StartBrowserTask extends AbstractTask {

	@Tunable (description="URL", gravity=1.0)
	public String url;

	@Tunable (description="HTML Text", context="nogui")
	public String text;

	@Tunable (description="Window Title", context="nogui")
	public String title = null;

	@Tunable (description="Window ID", context="nogui")
	public String id = null;

	@Tunable (description="Show debug tools", context="nogui")
	public boolean debug = false;

	final CyServiceRegistrar registrar;
	final CyBrowserManager manager;
	final boolean dialog;
  final CytoPanel cytoPanel = null;

	public StartBrowserTask(CyServiceRegistrar registrar, CyBrowserManager manager, boolean dialog) {
		this.registrar = registrar;
		this.manager = manager;
		this.dialog = dialog;
	}

	public void run(TaskMonitor monitor) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				CyBrowser br = manager.getBrowser(id);
				if (dialog) {
					SwingBrowser browser;
					if (br != null && br instanceof SwingBrowser)
						browser = (SwingBrowser) br;
					else if (br != null && br instanceof ResultsPanelBrowser) {
						manager.unregisterCytoPanel((ResultsPanelBrowser)br);
						browser = new SwingBrowser(registrar, title, debug);
					} else
						browser = new SwingBrowser(registrar, title, debug);
					browser.setVisible(true);
					if (url != null && url.length() > 3) {
						browser.loadURL(url);
					}
					br = (CyBrowser) browser;
				} else {
					ResultsPanelBrowser browser;
					if (br != null && br instanceof ResultsPanelBrowser)
						browser = (ResultsPanelBrowser) br;
					else
						browser = new ResultsPanelBrowser(registrar, title);

					if (url != null && url.length() > 3) {
						browser.loadURL(url);
					} else if (text != null) {
						browser.loadText(text);
					}
					manager.registerCytoPanel(browser);
					br = (CyBrowser) browser;
				}
				manager.addBrowser(br, id);
			}
		});

	}

	@ProvidesTitle
	public String getTitle() {
		return "Starting Cytoscape Web Browser";
	}
}
