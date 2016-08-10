package encryptor.encryptor.algorithms.appliers;

public class AppliersClassLoader extends ClassLoader{

	private ClassLoader parent;
	public AppliersClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if(name.equals(EncryptionApplier.class.getName())) {
			return EncryptionApplier.class;
		} else if(name.equals(DecryptionApplier.class.getName())) {
			return DecryptionApplier.class;
		} else if(name.equals(SplitDecryptionApplier.class.getName())) {
			return SplitDecryptionApplier.class;
		} else if(name.equals(SplitEncryptionApplier.class.getName())) {
			return SplitEncryptionApplier.class;
		}
		return parent.loadClass(name);
		
	}
	
}
