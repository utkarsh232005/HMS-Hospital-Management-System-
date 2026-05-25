package com.hospital.api.controller;

import com.hospital.api.model.DoctorEntity;
import com.hospital.api.repository.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorRepository repo;

    public DoctorController(DoctorRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<DoctorEntity> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorEntity> get(@PathVariable Integer id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public DoctorEntity create(@RequestBody DoctorEntity d) {
        return repo.save(d);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorEntity> update(@PathVariable Integer id, @RequestBody DoctorEntity d) {
        return repo.findById(id).map(existing -> {
            d.setId(existing.getId());
            return ResponseEntity.ok(repo.save(d));
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
