package com.example.upback.controllers;

import com.example.upback.dtos.ImageRecordDTO;
import com.example.upback.dtos.UserRecordDTO;
import com.example.upback.models.ImageModel;
import com.example.upback.models.UserModel;
import com.example.upback.repositories.ImageRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ImageController {
    @Autowired
    ImageRepository imageRepository;

    @PostMapping("/images")
    public ResponseEntity<Object> saveImage(@RequestBody @Valid ImageRecordDTO imageRecordDTO, HttpServletRequest request){
        String jwt = request.getHeader("jwt-token");
        if(jwt.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        var imageModel = new ImageModel();
        BeanUtils.copyProperties(imageRecordDTO,imageModel);

        imageModel.setUserId(UUID.fromString(jwt));

        return ResponseEntity.status(HttpStatus.CREATED).body(imageRepository.save(imageModel));
    }

    @GetMapping("/images")
    public ResponseEntity<List<ImageModel>> getAllImages(){
        List<ImageModel> imageModelList= imageRepository.findAll();

        if(!imageModelList.isEmpty()){
            for(ImageModel image : imageModelList){
                UUID id = image.getId();
                image.add(linkTo(methodOn(ImageController.class).geImageById(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(imageModelList);
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<Object> geImageById(@PathVariable(value="id") UUID id){
        Optional<ImageModel> imageModel = imageRepository.findById(id);
        if(imageModel.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found.");
        }
        imageModel.get().add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());

        return ResponseEntity.status(HttpStatus.OK).body(imageModel);
    }

    @DeleteMapping("/images/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value="id")  UUID id){
        Optional<ImageModel> imageModel = imageRepository.findById(id);

        if(imageModel.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found");
        }

        imageRepository.delete(imageModel.get());

        return ResponseEntity.status(HttpStatus.OK).body("Image deleted");
    }
}
