package br.com.luan.services;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import br.com.luan.controllers.BookController;
import br.com.luan.data.vo.v1.BookVO;
import br.com.luan.exceptions.RequiredObjectIsNullException;
import br.com.luan.exceptions.ResourceNotFoundException;
import br.com.luan.mapper.DozerMapper;
import br.com.luan.mapper.custom.PersonMapper;
import br.com.luan.model.Book;
import br.com.luan.repositories.BookRepository;

@Service
public class BookService {
	
	private Logger logger = Logger.getLogger(BookService.class.getName()); 
	
	@Autowired
	BookRepository repository;
	
	@Autowired
	PersonMapper mapper;

	public List<BookVO> findAll() {
		
		logger.info("Finding all books");
		
		var books=  DozerMapper.parseListObjects(repository.findAll(), BookVO.class);
		books
			.stream()
				.forEach(p -> {
					try {
						p.add(linkTo(methodOn(BookController.class).findById(p.getKey())).withSelfRel());
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
		
		return books;
	}

	public BookVO findById(Long id) throws Exception {
		
		logger.info("Finding one book");
				
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this id " + id));
		var vo = DozerMapper.parseObject(entity, BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public BookVO create(BookVO book) throws Exception {
		
		if (book == null) throw new RequiredObjectIsNullException();
				
		logger.info("Creating one book");
		
		var entity = DozerMapper.parseObject(book, Book.class);
		
		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public BookVO update(BookVO book) throws Exception {
	
		if (book == null) throw new RequiredObjectIsNullException();
		
		logger.info("Updating one book");
		
		var entity = repository.findById(book.getKey()).
				orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));
		
		entity.setAuthor(book.getAuthor());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());
		entity.setTitle(book.getTitle());
		
		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public void delete(Long id) {
		
		logger.info("Deleting one book");
		
		var entity = repository.findById(id).
				orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));
		
		repository.delete(entity);
	}


}