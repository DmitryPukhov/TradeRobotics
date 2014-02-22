package trading.app.adapter.quik;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

import trading.data.model.Level1New;

/**
 * Hibernate listener for data, exported from quik by odbc
 * 
 * @author dima
 * 
 */
public class DataListener {

	/**
	 * New level1 came from quik
	 * 
	 * @param level1
	 */
	@PostPersist
	@PostUpdate
	public void level1Received(Level1New level1) {
		System.out.println("Level1 received from quik");
	}
}
