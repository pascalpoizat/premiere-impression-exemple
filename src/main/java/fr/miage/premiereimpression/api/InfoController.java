package fr.miage.premiereimpression.api;

import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint technique : nom et version du back.
 * Sert de témoin pour vérifier que l'API répond (CI, conteneur, front).
 */
@RestController
@RequestMapping("/api/v1/info")
public class InfoController {

    private final BuildProperties buildProperties;

    public InfoController(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @GetMapping
    public InfoDto info() {
        return new InfoDto(buildProperties.getName(), buildProperties.getVersion());
    }
}
