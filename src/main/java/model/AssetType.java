package model;

public enum AssetType {
	STOCK,
	CALL,
	PUT;

	public static AssetType fromString(String string) {
		for (AssetType assetType : AssetType.values()) {
			if (assetType.toString().equals(string)) {
				return assetType;
			}
		}
		return null;
	}
}
