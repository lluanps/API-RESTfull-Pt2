package br.com.luan.services;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import br.com.luan.controllers.PersonController;
import br.com.luan.data.vo.v1.PersonVO;
import br.com.luan.data.vo.v2.PersonVOV2;
import br.com.luan.exceptions.RequiredObjectIsNullException;
import br.com.luan.exceptions.ResourceNotFoundException;
import br.com.luan.mapper.DozerMapper;
import br.com.luan.mapper.custom.PersonMapper;
import br.com.luan.model.Person;
import br.com.luan.repositories.PersonRepository;

@Service
public class PersonService {
	
	private Logger logger = Logger.getLogger(PersonService.class.getName()); 
	
	@Autowired
	PersonRepository repository;
	
	@Autowired
	PersonMapper mapper;

	public List<PersonVO> findAll() {
		
		logger.info("Finding all people");
		
		var persons=  DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
		persons
			.stream()
				.forEach(p -> {
					try {
						p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel());
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
		
		return persons;
	}

	public PersonVO findById(Long id) throws Exception {
		
		logger.info("Finding one person");
				
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this id " + id));
		var vo = DozerMapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public PersonVO create(PersonVO person) throws Exception {
		
		if (person == null) throw new RequiredObjectIsNullException();
				
		logger.info("Creating one person");
		
		var entity = DozerMapper.parseObject(person, Person.class);
		
		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public PersonVOV2 createV2(PersonVOV2 person) {
		
		logger.info("Creating one person");
		
		var entity = mapper.convertVoToEntity(person);
		
		var vo = mapper.convertEntityToVo(repository.save(entity));
		return vo;
	}
	
	public PersonVO update(PersonVO person) throws Exception {
	
		if (person == null) throw new RequiredObjectIsNullException();
		
		logger.info("Updating one person");
		
		var entity = repository.findById(person.getKey()).
				orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));
		
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		
		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public void delete(Long id) {
		
		logger.info("Deleting one person");
		
		var entity = repository.findById(id).
				orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));
		
		repository.delete(entity);
	}


}