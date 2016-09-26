package n26.bonner.andre;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactionservice")
public class N26TransactionControllerRS {

	@RequestMapping(value = "/transaction/{transaction_id}", method = RequestMethod.GET)
	TransactionRequestResponse get(@PathVariable Long transaction_id) {
		return new TransactionRequestResponse(
							N26TransactionsApplication
								.get(transaction_id)
								.orElseThrow(() -> new TransactionNotFoundException(transaction_id+"")));
	}
	 
	@RequestMapping(value = "/types/{type}", method = RequestMethod.GET)
	List<Long> types(@PathVariable String type) {
		return N26TransactionsApplication.findAll(Optional.of(type));
	}
	
	@RequestMapping(value = "/sum/{transaction_id}", method = RequestMethod.GET)
	HashMap<String,Double> sum(@PathVariable Long transaction_id) {
		Double value = N26TransactionsApplication.sum(transaction_id);
		HashMap<String,Double> result = new HashMap<String,Double>(1);
		result.put("sum", value);
		return result;
	}
	
	@RequestMapping(value = "/transaction/{transaction_id}", method = RequestMethod.POST)
    public HashMap<String,String> create(@PathVariable Long transaction_id, @RequestBody TransactionRequestResponse value, HttpServletResponse response) {
		HashMap<String,String> status = new HashMap<String,String>(1);
		//If the creation succeeds return created status
		if(N26TransactionsApplication.create(transaction_id, value.getAmount(), value.getType(),Optional.ofNullable(value.getParentId()))){
			response.setStatus(HttpServletResponse.SC_CREATED);
			status.put("status", "ok");
			return status;
		}//Else return conflict as it wasn't added to the database
		else{
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			status.put("status", "conflict");
			return status;
		}
    }
  
	
	@RequestMapping(value = "/transaction/{transaction_id}", method = RequestMethod.PUT)
    public TransactionRequestResponse update(@PathVariable Long transaction_id, @RequestBody TransactionRequestResponse value, HttpServletResponse response) {
	
		Optional<N26Transaction> result = N26TransactionsApplication.update(transaction_id,
											Optional.ofNullable(value.getAmount()), 
													Optional.ofNullable(value.getType()),
													N26TransactionsApplication.get(value.getParentId()));
		return new TransactionRequestResponse(result.
					orElseThrow(() -> new TransactionNotFoundException(transaction_id+"")));
    }
	
    
    @RequestMapping(value = "/transaction/{transaction_id}", method = RequestMethod.DELETE)
    public HashMap<String,String> delete(@PathVariable Long transaction_id ,HttpServletResponse response) {
    	HashMap<String,String> status = new HashMap<String,String>(1);
		//If the creation succeeds return created status
		if(N26TransactionsApplication.delete(transaction_id)){
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			status.put("status", "ok");
			return status;
		}//Else return conflict as it wasn't added to the database
		else{
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			status.put("status", "conflict");
			return status;
		}
    }
	
	@ResponseStatus(value=HttpStatus.NOT_FOUND,reason="This transaction was not found in the system")
	@ExceptionHandler(TransactionNotFoundException.class)
	public void exceptionHandler(){
	
	}
}

class TransactionRequestResponse extends Transaction implements Serializable {
	
	private Long parentId;
	//No argument constructor for Jackson
	public TransactionRequestResponse(){} 
	//Constructor for mapping from internal data structure
	public TransactionRequestResponse(N26Transaction data){
		BeanUtils.copyProperties(data, this); // Copies all properties that are shared automatically
		setParentId(data.getParent().map(x -> x.getId()).orElse(0L));
	} 
	
	public Long getParentId() {
		return parentId;
	}
	
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class TransactionNotFoundException extends RuntimeException {

	public TransactionNotFoundException(String transactionId) {
		super("could not find transaction '" + transactionId + "'.");
	}
}
