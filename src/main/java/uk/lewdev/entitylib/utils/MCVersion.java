package uk.lewdev.entitylib.utils;

public enum MCVersion {
	V1_10("1.10"),
	V1_11("1.11"),
	V1_12("1.12"),
	V1_13("1.13"),
	V1_14("1.14"),
	V1_15("1.15"),
    V1_16("1.16");
	
	protected static MCVersion CUR_VERSION = V1_10;
	
	static {
		String curVerStr = ReflectionUtil.getVersion();
		
		for (MCVersion version : MCVersion.values()) {
			String versionName = version.name();
			if(curVerStr.toLowerCase().contains(versionName.toLowerCase())) {
				CUR_VERSION = version;
				break;
			}
		}
	}
	
	protected String versionString;
	
	MCVersion(String verStr) {
		this.versionString = verStr;
	}
	
	public static MCVersion CUR_VERSION() { return CUR_VERSION; }
	
	public String toMinecraftVersionStr() {
		return this.versionString;
	}
	
	public boolean isAfter1_14() {
		return this.ordinal() > MCVersion.V1_14.ordinal();
	}
	
	public boolean isAfterOr1_16() {
	    return this.ordinal() >= MCVersion.V1_16.ordinal();
	}
}