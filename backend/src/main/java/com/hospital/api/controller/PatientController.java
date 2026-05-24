package com.hospital.api.controller;

import com.hospital.api.model.PatientEntity;
import com.hospital.api.repository.PatientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientRepository repo;

    public PatientController(PatientRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<PatientEntity> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientEntity> get(@PathVariable Integer id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public PatientEntity create(@RequestBody PatientEntity p) {
        return repo.save(p);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientEntity> update(@PathVariable Integer id, @RequestBody PatientEntity p) {
        return repo.findById(id).map(existing -> {
            p.setId(existing.getId());
            return ResponseEntity.ok(repo.save(p));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!repo.existsById(id))
            return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
