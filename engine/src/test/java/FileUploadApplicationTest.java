import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles("test")
@ContextConfiguration(classes = FileUploadApplication.class)
public class FileUploadApplicationTest {
    @Autowired
    private FileUploadApplication application;

    @Test
    public void contextLoads() {
        // Ensure that the application context loads successfully
    }
}
