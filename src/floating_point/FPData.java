package floating_point;

public class FPData {
	
	public enum Permissions {
		Read_Only,
		Write;
	}
	
	private float val;
	private Permissions per;
	
	public FPData() {
		this(0);
	}
	
	public FPData(float value) {
		this(value, Permissions.Write);
	}
	

	
	public FPData(float value, Permissions permission) {
		val = value;
		per = permission;
	}
	
	public float getValue() {
		return val;
	}
	
	public Permissions getPermissions() {
		return per;
	}
	
	@Override
	public int hashCode() {
		return new Float(val).hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof FPData && this.val == ((FPData) other).val;
	}
	
	@Override
	public String toString() {
		return new Float(val).toString();
	}
	
}
