package fr.miage.premiereimpression.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.springframework.boot.info.BuildProperties;

/** Test unitaire : aucun contexte Spring, on instancie le contrôleur à la main. */
class InfoControllerTest {

    @Test
    void returnsNameAndVersionFromBuildProperties() {
        Properties props = new Properties();
        props.setProperty("name", "premiere-impression-back");
        props.setProperty("version", "1.2.3");
        InfoController controller = new InfoController(new BuildProperties(props));

        InfoDto info = controller.info();

        assertThat(info.name()).isEqualTo("premiere-impression-back");
        assertThat(info.version()).isEqualTo("1.2.3");
    }
}
