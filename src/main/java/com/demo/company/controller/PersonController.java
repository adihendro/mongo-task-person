package com.demo.company.controller;

import com.demo.base.BaseResponse;
import com.demo.base.ListBaseResponse;
import com.demo.base.Metadata;
import com.demo.base.SingleBaseResponse;
import com.demo.company.entity.Address;
import com.demo.company.entity.Person;
import com.demo.company.service.PersonService;
import com.demo.dto.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = PersonControllerPath.BASE_PATH)

public class PersonController {

    @Autowired
    private PersonService personService;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse create(@RequestParam String storeId, @RequestParam String channelId,
                               @RequestParam String clientId, @RequestParam String requestId,
                               @RequestBody PersonCreateRequest request) throws Exception {
        this.personService.create(toPerson(request));
        return new BaseResponse(null, null, true, requestId);
    }

    @RequestMapping(value = PersonControllerPath.UPDATE_BY_CODE, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse updateByPersonCode(@RequestParam String storeId, @RequestParam String channelId,
                                      @RequestParam String clientId, @RequestParam String requestId, @PathVariable String personCode,
                                      @RequestBody PersonUpdateRequest request) throws Exception {
        this.personService.update(personCode, toPerson(request));
        return new BaseResponse(null, null, true, requestId);
    }

    @RequestMapping(value = PersonControllerPath.UPDATE_NAME_BY_CODE, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse updateNameByPersonCode(@RequestParam String storeId, @RequestParam String channelId,
                                          @RequestParam String clientId, @RequestParam String requestId, @PathVariable String personCode,
                                          @RequestBody PersonUpdateNameRequest request) throws Exception {
        Person person = Person.builder().build();
        BeanUtils.copyProperties(request, person);
        this.personService.updateName(personCode, person);
        return new BaseResponse(null, null, true, requestId);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListBaseResponse<PersonResponse> findPersons(@RequestParam String storeId, @RequestParam String channelId,
                                                            @RequestParam String clientId, @RequestParam String requestId,
                                                            @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size)
            throws Exception {
        Pageable pageable = PageRequest.of(page, size);
        Page<Person> persons = this.personService.find(pageable);
        List<PersonResponse> personResponses = persons.getContent().stream().map(this::toPersonResponse).collect(Collectors.toList());
        return new ListBaseResponse<>(null, null, true, requestId, personResponses,
                new Metadata(page, size, persons.getTotalElements()));
    }

    @RequestMapping(value = PersonControllerPath.FIND_BY_CODE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SingleBaseResponse<PersonResponse> findByPersonCode(@RequestParam String storeId,
                                                            @RequestParam String channelId, @RequestParam String clientId, @RequestParam String requestId,
                                                          @PathVariable String personCode) throws Exception {
        Person person = this.personService.findByCode(personCode);
        PersonResponse personResponse = Optional.ofNullable(person).map(this::toPersonResponse).orElse(null);
        return new SingleBaseResponse<>(null, null, true, requestId, personResponse);
    }

    @RequestMapping(value = PersonControllerPath.DELETE_BY_CODE, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse deleteByPersonCode(@RequestParam String storeId, @RequestParam String channelId,
                                      @RequestParam String clientId, @RequestParam String requestId,
                                      @RequestParam String personCode) throws Exception {
        this.personService.deleteByPersonCode(personCode);
        return new BaseResponse(null, null, true, requestId);
    }


    private PersonResponse toPersonResponse(Person person) {
        return Optional.ofNullable(person).map(e -> {
            PersonResponse personResponse = PersonResponse.builder().build();
            BeanUtils.copyProperties(e, personResponse);
            personResponse.setAddresses(toAddressResponse(person.getAddresses()));
            return personResponse;
        }).orElse(null);
    }

    private List<AddressResponse> toAddressResponse(List<Address> request) {
        return request.stream().map(d -> {
                AddressResponse addressResponse = new AddressResponse();
                BeanUtils.copyProperties(d, addressResponse);
            return addressResponse;
        }).collect(Collectors.toList());
    }

    private Person toPerson(PersonUpdateRequest request) {
        return Optional.ofNullable(request).map(e -> {
            Person person = Person.builder().build();
            BeanUtils.copyProperties(e, person);
            person.setAddresses(toAddress(request.getAddresses()));
            return person;
        }).orElse(null);
    }

    private Person toPerson(PersonCreateRequest request) {
        return Optional.ofNullable(request).map(e -> {
            Person person = Person.builder().build();
            BeanUtils.copyProperties(e, person);
            person.setAddresses(toAddress(request.getAddresses()));
            return person;
        }).orElse(null);
    }

    private List<Address> toAddress(List<AddressRequest> request) {
        return request.stream().map(d -> {
            Address address = new Address();
            BeanUtils.copyProperties(d, address);
            return address;
        }).collect(Collectors.toList());
    }
}
