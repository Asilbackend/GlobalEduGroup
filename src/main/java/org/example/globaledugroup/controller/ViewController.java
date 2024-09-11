package org.example.globaledugroup.controller;

import lombok.RequiredArgsConstructor;
import org.example.globaledugroup.entity.view.Attachment;
import org.example.globaledugroup.entity.view.Jobs;
import org.example.globaledugroup.entity.view.University;
import org.example.globaledugroup.repository.AttachmentRepository;
import org.example.globaledugroup.repository.JobsRepository;
import org.example.globaledugroup.repository.UniversityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/view")
public class ViewController {
    @Value("${server.url}")
    String url;
    private final UniversityRepository universityRepository;
    private final AttachmentRepository attachmentRepository;
    private final JobsRepository jobsRepository;

    @GetMapping("/getUniversities")
    public HttpEntity<?> getUniversities() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(universityRepository.findAll(), headers, HttpStatus.OK);
    }

    @DeleteMapping("/university/{id}")
    public HttpEntity<?> deleteUniversity(@PathVariable Long id) {
        Optional<University> universityOptional = universityRepository.findById(id);
        if (universityOptional.isEmpty()) {
            throw new RuntimeException("univer not found");
        }
        University university = universityOptional.get();
        Attachment attachment = university.getAttachment();
        universityRepository.deleteById(id);
        if (attachment != null) {
            attachmentRepository.deleteById(attachment.getId());
            Path path = Paths.get("src/main/resources/templates/" + attachment.getFileName());
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok("ok");
    }

    @PostMapping("/university")
    @Transactional
    public HttpEntity<?> addUniversity(@RequestParam MultipartFile file, @RequestParam String name, @RequestParam String place) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get("src/main/resources/templates/" + fileName);
        Files.write(path, file.getInputStream().readAllBytes());
        Attachment save = attachmentRepository.save(Attachment.builder().fileName(fileName).url(url + "api/view/images/%s".formatted(fileName)).build());
        University university = universityRepository.save(University.builder().attachment(save).name(name).place(place).build());
        return ResponseEntity.ok(university);
    }


    @PostMapping("/jobs")
    public List<String> saveJob(@RequestParam("files") MultipartFile[] files,
                                @RequestParam String name,
                                @RequestParam String description,
                                @RequestParam String whereWork,
                                @RequestParam String whereStudy) throws IOException {
        List<String> filenames = new ArrayList<>();
        System.out.println("keldi");
        List<Attachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get("src/main/resources/templates/" + filename);
            attachments.add(attachmentRepository.save(Attachment.builder().fileName(filename).url(url + "api/view/images/%s".formatted(filename)).build()));
            Files.write(path, file.getBytes());
            filenames.add(filename);
        }
        Jobs save = jobsRepository.save(Jobs.builder().description(description).whereStudy(whereStudy).whereWork(whereWork).name(name).attachment(attachments).build());
        System.out.println("saqlangan kasb: " + save);
        return filenames; // Saqlangan fayllarning nomlarini qaytarish
    }

    @GetMapping("/getJobs")
    public HttpEntity<?> getJobs() {
        return ResponseEntity.ok(jobsRepository.findAll());
    }

    @DeleteMapping("/jobs/{id}")
    public HttpEntity<?> deleteJob(@PathVariable Long id) {
        Optional<Jobs> jobsOptional = jobsRepository.findById(id);
        if (jobsOptional.isEmpty()) {
            throw new RuntimeException("not found user");
        }
        Jobs jobs = jobsOptional.get();
        for (Attachment attachment : jobs.getAttachment()) {
            if (attachment != null) {
                attachmentRepository.deleteById(attachment.getId());
                Path path = Paths.get("src/main/resources/templates/" + attachment.getFileName());
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        jobsRepository.deleteById(jobs.getId());
        return ResponseEntity.ok("deleted success");
    }

    @GetMapping("/jobs/{id}")
    public HttpEntity<?> getJob(@PathVariable Long id) {
        Optional<Jobs> jobsOptional = jobsRepository.findById(id);
        if (jobsOptional.isEmpty()) {
            throw new RuntimeException("not found user");
        }
        Jobs jobs = jobsOptional.get();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/getServices")
    public HttpEntity<?> getServices() {
        return ResponseEntity.ok(jobsRepository.findAll());
    }

    @GetMapping("/images/{imageName}")
    public HttpEntity<?> getImage(@PathVariable String imageName) throws RuntimeException {
        Path path = Paths.get("src/main/resources/templates/" + imageName);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg"); // Rasm turi, masalan JPEG uchun
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"image.jpg\""); // Faylni yuklab olishda taklif qilinadigan nom

        try {
            byte[] bytes = Files.readAllBytes(path);
            if (!Files.exists(path))
                throw new RuntimeException("ushbu nom boyicha fayl topilmadi : " + imageName);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
