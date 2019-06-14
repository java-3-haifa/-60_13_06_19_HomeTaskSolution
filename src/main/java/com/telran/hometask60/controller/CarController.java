package com.telran.hometask60.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telran.hometask60.controller.dto.SuccessResponseDto;
import com.telran.hometask60.controller.dto.CarDto;
import com.telran.hometask60.controller.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class CarController {
    private ConcurrentHashMap<String, CarDto> map = new ConcurrentHashMap<>();
    private ObjectMapper mapper = new ObjectMapper();
    private final String serverErrorMsg = "{\"message\":\"server error!\"}";

    @PostMapping("car")
    public ResponseEntity<String> addCar(@RequestBody CarDto car) {
        String res;
        try {
            if (map.putIfAbsent(car.serialNumber, car) == null) {
                SuccessResponseDto responseDto = new SuccessResponseDto();
                responseDto.message = "Car added!";
                res = mapper.writeValueAsString(responseDto);
                return ResponseEntity.ok(res);
            }
            ErrorDto error = new ErrorDto();
            error.message = "Car with serialNumber: " + car.serialNumber + " already exist";

            res = mapper.writeValueAsString(error);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(res);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(serverErrorMsg);
        }
    }

    @GetMapping("car")
    public ResponseEntity<String> getAllCars() {
        try {
            String res = mapper.writeValueAsString(map.values());
            return ResponseEntity.ok(res);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(serverErrorMsg);
        }
    }

    @DeleteMapping("car/{serialNumber}")
    public ResponseEntity<String> removeCarBySerialNumber(@PathVariable("serialNumber") String serialNumber) {
        try {
            if (map.remove(serialNumber) == null) {
                ErrorDto error = new ErrorDto();
                error.message = "Car with serialNumber: " + serialNumber + " does not exist";
                String res = mapper.writeValueAsString(error);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }
            SuccessResponseDto response = new SuccessResponseDto();
            response.message = "Car with serialNumber: " + serialNumber + " was removed";
            return ResponseEntity.ok(mapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(serverErrorMsg);
        }
    }

    @GetMapping("car/{serialNumber}")
    public ResponseEntity<String> getCarBySerialNumber(@PathVariable("serialNumber") String serialNumber) {
        CarDto dto = map.get(serialNumber);
        try {
            if (dto == null) {
                ErrorDto error = new ErrorDto();
                error.message = "Car with serialNumber: " + serialNumber + " does not exist";
                String res = mapper.writeValueAsString(error);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }
            return ResponseEntity.ok(mapper.writeValueAsString(dto));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(serverErrorMsg);
        }
    }

    @PutMapping("car")
    public ResponseEntity<String> updateCar(@RequestBody CarDto car) {
        try {
            if (map.replace(car.serialNumber, car) == null) {
                ErrorDto error = new ErrorDto();
                error.message = "Car with serialNumber: " + car.serialNumber + " does not exist";
                String res = mapper.writeValueAsString(error);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }
            SuccessResponseDto response = new SuccessResponseDto();
            response.message = "Car with serialNumber: " + car.serialNumber + " was updated";
            return ResponseEntity.ok(mapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(serverErrorMsg);
        }
    }
}
