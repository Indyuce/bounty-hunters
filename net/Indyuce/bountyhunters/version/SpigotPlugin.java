package net.Indyuce.bountyhunters.version;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.plugin.java.JavaPlugin;

/*
 * @author iShadey
 *
 * Class created to check updates using SpigotMC's legacy API.
 *
 */

public class SpigotPlugin {
	private int projectId;
	private URL checkURL;
	private String newVersion;
	private JavaPlugin plugin;

	public SpigotPlugin(JavaPlugin plugin, int projectId) {
		this.plugin = plugin;
		this.newVersion = plugin.getDescription().getVersion();
		this.projectId = projectId;
		try {
			this.checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectId);
		} catch (MalformedURLException e) {
		}
	}

	public String getResourceURL() {
		return "https://www.spigotmc.org/resources/" + projectId;
	}

	public boolean isOutOfDate() {
		try {
			URLConnection con = checkURL.openConnection();
			this.newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
			return new PluginVersion(newVersion).higherThan(new PluginVersion(plugin.getDescription().getVersion()));
		} catch (IOException e) {
			return false;
		}
	}

	public String[] getOutOfDateMessage() {
		return new String[] { "A new update is available: " + newVersion + " (you are running " + plugin.getDescription().getVersion() + ")", "Download it here: " + getResourceURL() + " " };
	}
}
