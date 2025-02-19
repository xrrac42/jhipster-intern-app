package br.com.elfotec.web.rest;

import br.com.elfotec.domain.Pessoa;
import br.com.elfotec.repository.PessoaRepository;
import br.com.elfotec.service.PessoaService;
import br.com.elfotec.service.dto.PessoaDTO;
import br.com.elfotec.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link br.com.elfotec.domain.Pessoa}.
 */
@RestController
@RequestMapping("/api")
public class PessoaResource {

    private final Logger log = LoggerFactory.getLogger(PessoaResource.class);

    private static final String ENTITY_NAME = "pessoa";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PessoaService pessoaService;

    private final PessoaRepository pessoaRepository;

    public PessoaResource(PessoaService pessoaService, PessoaRepository pessoaRepository) {
        this.pessoaService = pessoaService;
        this.pessoaRepository = pessoaRepository;
    }

    /**
     * {@code POST  /pessoas} : Create a new pessoa.
     *
     * @param pessoaDTO the pessoaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pessoaDTO, or with status {@code 400 (Bad Request)} if the pessoa has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pessoas")
    public ResponseEntity<PessoaDTO> createPessoa(@Valid @RequestBody PessoaDTO pessoaDTO) throws URISyntaxException {
        log.debug("REST request to save Pessoa : {}", pessoaDTO);
        if (pessoaDTO.getId() != null) {
            throw new BadRequestAlertException("A new pessoa cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PessoaDTO result = pessoaService.save(pessoaDTO);
        return ResponseEntity
            .created(new URI("/api/pessoas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /pessoas/:id} : Updates an existing pessoa.
     *
     * @param id the id of the pessoaDTO to save.
     * @param pessoaDTO the pessoaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaDTO,
     * or with status {@code 400 (Bad Request)} if the pessoaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pessoaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pessoas/{id}")
    public ResponseEntity<PessoaDTO> updatePessoa(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PessoaDTO pessoaDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Pessoa : {}, {}", id, pessoaDTO);
        if (pessoaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        boolean pessoaExists = pessoaRepository.existsById(id);

        if (!pessoaExists) {
            return createPessoa(pessoaDTO);
        }

        PessoaDTO result = pessoaService.save(pessoaDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pessoaDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /pessoas/:id} : Partial updates given fields of an existing pessoa, field will ignore if it is null
     *
     * @param id the id of the pessoaDTO to save.
     * @param pessoaDTO the pessoaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaDTO,
     * or with status {@code 400 (Bad Request)} if the pessoaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pessoaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pessoaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pessoas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PessoaDTO> partialUpdatePessoa(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PessoaDTO pessoaDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Pessoa partially : {}, {}", id, pessoaDTO);
        if (pessoaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pessoaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PessoaDTO> result = pessoaService.partialUpdate(pessoaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pessoaDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /pessoas} : get all the pessoas.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoas in body.
     */
    @GetMapping("/pessoas")
    public ResponseEntity<List<PessoaDTO>> getAllPessoas(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Pessoas");

        Page<PessoaDTO> page = pessoaService.findAllNotExcluded(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pessoas/:id} : get the "id" pessoa.
     *
     * @param id the id of the pessoaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pessoaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pessoas/{id}")
    public ResponseEntity<PessoaDTO> getPessoa(@PathVariable Long id) {
        log.debug("REST request to get Pessoa : {}", id);
        Optional<PessoaDTO> pessoaDTO = pessoaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pessoaDTO);
    }

    /**
     * {@code DELETE  /pessoas/:id} : delete the "id" pessoa.
     *
     * @param id the id of the pessoaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */

    @DeleteMapping("/pessoas/{id}")
    public ResponseEntity<Void> deletePessoa(@PathVariable Long id) {
        log.debug("REST request to delete Pessoa: {}", id);

        pessoaService
            .findOne(id)
            .ifPresent(pessoaDTO -> {
                pessoaDTO.setDataExclusao(Instant.now());
                pessoaService.save(pessoaDTO);
            });

        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
