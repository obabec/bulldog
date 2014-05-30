package org.bulldog.beagleboneblack.sysfs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.bulldog.core.util.BulldogUtil;

public class SysFs {

	private static final int WAIT_TIMEOUT_MS = 5000;
	
	private String SYSFS_DEVICES_PATH = "/sys/devices";
	
	public SysFs() {
		
	}
	
	public File[] getFilesInPath(String path, final String namePattern) {
		File root = new File(path);
		File[] files = root.listFiles(new FileFilter() {  
		    public boolean accept(File pathname) {  
		        return pathname.getName().startsWith(namePattern);
		    }  
		});
		
		return files;
	}
	
	public File getCapeManager() {
		return getFilesInPath(SYSFS_DEVICES_PATH, "bone_capemgr")[0];
	}
	
	public File getCapeManagerSlots() {
		return getFilesInPath(getCapeManager().getAbsolutePath(), "slots")[0];
	}
	
	public int getSlotNumber(String namePattern) {
		List<String> slots = readSlots();
		for(int i = 0; i < slots.size(); i++) {
			String slotContent = slots.get(i);
			if(slotContent.contains(namePattern)) {
				return Integer.parseInt(slotContent.substring(0, slotContent.indexOf(":")).trim());
			}
		}
		
		return - 1;
	}
	
	public boolean isSlotLoaded(int slotIndex) {
		String slot = readSlots().get(slotIndex);
		return slot.charAt(11) == 'L';
	}
	
	public List<String> readSlots() {
		List<String> buffer = new ArrayList<String>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(getCapeManagerSlots()));
			String line = null;
			while((line = reader.readLine()) != null) {
				buffer.add(line);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return buffer;
	}
	
	public File findOcpDevice(final String namePattern) {
		File[] ocpDirectories = getFilesInPath(SYSFS_DEVICES_PATH, "ocp");
		for(File file : ocpDirectories) {
			File[] files = getFilesInPath(file.getAbsolutePath(), namePattern);
			if(files != null && files.length > 0) {
				return files[0];
			}
			
		}
		return null;
	}
	
	public void echo(String path, Object value) {
		echo(path, String.valueOf(value));
	}
	 
	
	public void echo(String path, String value) {
		try {
			waitForFileCreation(path, WAIT_TIMEOUT_MS);
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write(value);
			BulldogUtil.sleepMs(10);
			writer.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void removeSlot(int number) {
		echo(getCapeManagerSlots().getAbsolutePath(), "-" + number);
	}
	
	public void createSlotIfNotExists(String deviceName) {
		if(getSlotNumber(deviceName) < 0) {
			echo(getCapeManagerSlots().getAbsolutePath(), deviceName);
			waitForSlotCreation(deviceName, WAIT_TIMEOUT_MS);
		} 
	}

	private void waitForSlotCreation(String deviceName, long waitMillis) {
		long startWaitingTime = System.currentTimeMillis();
		while(getSlotNumber(deviceName) < 0) {
			//wait until the device appears
			BulldogUtil.sleepMs(10);
			long millisecondsInWait = System.currentTimeMillis()  - startWaitingTime;
			if(millisecondsInWait >= waitMillis) {
				throw new RuntimeException("Could not create device for " + deviceName + " within " + waitMillis + " milliseconds. Aborting.");
			}
		}
	}
	
	private void waitForFileCreation(String filePath, long waitMillis) {
		long startWaitingTime = System.currentTimeMillis();
		File file = new File(filePath);
		while(!file.exists()) {
			//wait until the device appears
			BulldogUtil.sleepMs(10);
			
			long millisecondsInWait = System.currentTimeMillis()  - startWaitingTime;
			if(millisecondsInWait >= waitMillis) {
				throw new RuntimeException("Could not find file " + filePath + " within " + waitMillis + " milliseconds. Aborting.");
			}
		}
	}
	
	public void removeSlotOfDevice(String deviceName) {
		int slotNumber = getSlotNumber(deviceName);
		if(slotNumber >= 0) {
			removeSlot(slotNumber);
		}
	}
}
