package com.example.upback.controllers;



import com.example.upback.dtos.UserRecordDTO;
import com.example.upback.models.UserModel;
import com.example.upback.repositories.UserRepository;
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
public class UserController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("/users")
    public ResponseEntity<UserModel> saveUser(@RequestBody @Valid UserRecordDTO userRecordDTO){
        var userModel = new UserModel();
        BeanUtils.copyProperties(userRecordDTO,userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(userModel));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserModel>> getAllUsers(){
        List<UserModel> userModelList= userRepository.findAll();

        if(!userModelList.isEmpty()){
            for(UserModel user : userModelList){
                UUID id = user.getId();
                user.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(userModelList);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable(value="id") UUID id){
        Optional<UserModel> user = userRepository.findById(id);
        if(user.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        user.get().add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable(value="id")  UUID id, @RequestBody @Valid UserRecordDTO userRecordDTO){
        Optional<UserModel> user = userRepository.findById(id);

        if(user.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        var userModel = user.get();

        BeanUtils.copyProperties(userRecordDTO,userModel);

        return ResponseEntity.status(HttpStatus.OK).body(userRepository.save(userModel));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value="id")  UUID id){
        Optional<UserModel> user = userRepository.findById(id);

        if(user.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        userRepository.delete(user.get());

        return ResponseEntity.status(HttpStatus.OK).body("User deleted");
    }
}
