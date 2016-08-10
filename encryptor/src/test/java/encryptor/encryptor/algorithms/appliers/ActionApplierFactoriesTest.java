package encryptor.encryptor.algorithms.appliers;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;
import org.omg.CORBA.portable.ApplicationException;

import encryptor.encryptor.algorithms.EncryptionAlgorithm;

@RunWith(Parameterized.class)
public class ActionApplierFactoriesTest {

	private static Class<?> applierClass;
	private static Class<?> factoryClass;
	
	
	public ActionApplierFactoriesTest(Class<?> toTest, Class<?> expectedResultsClass) {
		applierClass = expectedResultsClass;
		factoryClass = toTest;
	}
	
	@Parameters
	public static Collection<Object[]> supplyParams() {
		List<Object[]> args = new ArrayList<Object[]>();
		args.addAll(Arrays.asList(
				new Object[] {EncryptionApplierFactory.class,EncryptionApplier.class},
				new Object[] {DecryptionApplierFactory.class, DecryptionApplier.class},
				new Object[] {SplitDecryptionApplierFactory.class, SplitDecryptionApplier.class},
				new Object[] {SplitEncryptionApplierFactory.class, SplitEncryptionApplier.class}
				));
		return args;
	}
	
	@Test (expected = NullPointerException.class)
	public void throwsNullPointerExceptionOnNullAlg() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ActionApplierFactory factory = (ActionApplierFactory) factoryClass.newInstance();
		factory.get(null);
	}
	
	@Test 
	public void returnsApplierOfExpectedClass() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ActionApplierFactory factory = (ActionApplierFactory) factoryClass.newInstance();
		EncryptionAlgorithm mockAlgorithm = Mockito.mock(EncryptionAlgorithm.class);
		ActionApplier applier = factory.get(mockAlgorithm);
		assertEquals(applier.getClass().getName(),applierClass.getName());
		
	}
}
