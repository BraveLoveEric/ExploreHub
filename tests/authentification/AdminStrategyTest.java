package authentification;

import authentification.loginProcess.AdminStrategy;
import models.Admin;
import org.junit.Before;
import org.junit.Test;
import persistenceComponent.AdminConnectionSingleton;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.Assert.*;

/**
 * Test class for the {@link AdminStrategy}
 *
 * @author Gheorghe Mironica
 */
public class AdminStrategyTest {

    private EntityManager entityManager;
    private AdminConnectionSingleton con;

    /**
     * Establish data base connection before starting
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        con = AdminConnectionSingleton.getInstance();
        entityManager = con.getManager();
    }

    /**
     * Tests the method {@link #getAccount()}
     */
    @Test
    public void getAccount() {
        @SuppressWarnings("JpaQueryApiInspection")
        TypedQuery<Admin> tq2 = entityManager.createNamedQuery(
                "Admin.findAdminByEmailPass",
                Admin.class);
        tq2.setParameter("email", "hochschule@hs-ulm.de");
        tq2.setParameter("password", "1234");
        assertTrue(tq2.getSingleResult().getId()==777);
    }
}