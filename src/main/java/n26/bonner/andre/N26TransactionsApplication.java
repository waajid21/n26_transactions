package n26.bonner.andre;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Number 26 Java Code Challenge
 * 
 * We would like to have a RESTful web service that stores some transactions (in memory is fine)
 * and returns information about those transactions.
 * The transactions to be stored have a type and an amount. The service should support returning all
 * transactions of a type. Also, transactions can be linked to each other (using a "parent_id") and we
 * need to know the total amount involved for all transactions linked to a particular transaction.
 * 1) Please complete it using Java and in 3 consecutive days.
 * 2) Code does not need to be deployable
 * 3) We prefer that you post code on Github or Bitbucket, so we can review the code.
 * 4) Do not use SQL.
 * In general we are looking for a good implementation, code quality and how the implementation is
 * tested. Some discussion about asymptotic behaviour would also be appreciated
 * 
 * @author Andre Darian Bonner
 * 
 */
@SpringBootApplication
public class N26TransactionsApplication {
	
	//For the lack of a db, i still want constant time operations O(1)
	public static HashSet<N26Transaction> transactions = new HashSet<N26Transaction>();
	
	public static void main(String[] args) {
		SpringApplication.run(N26TransactionsApplication.class, args);
	}
	
	@Bean
	CommandLineRunner runner(){
		return args ->{
			//load transactions
			create((long) 1, 45.0, "credit", Optional.empty());
			create((long) 2, 1000.0, "credit", Optional.empty());
			create((long) 3, -2500.0, "debit", Optional.empty());
			create((long) 4, 525.0, "credit", Optional.empty());
			create((long) 5, -375.0, "debit", Optional.empty());
			create((long) 6, 34.57, "credit", Optional.empty());
			create((long) 7, 34.57, "credit", Optional.empty());
			create((long) 8, -634.57, "debit", Optional.empty());
			create((long) 9, 34.57, "credit", Optional.empty());
			create((long) 10, -57.17, "debit", Optional.empty());
			//set children
			update((long) 1, Optional.empty(),Optional.empty(), get((long)5));
			update((long) 2, Optional.empty(),Optional.empty(), get((long)5));
			update((long) 3, Optional.empty(),Optional.empty(), Optional.empty());
			update((long) 4, Optional.empty(),Optional.empty(), get((long)4));
			update((long) 5, Optional.empty(),Optional.empty(), get((long)6));
			update((long) 6, Optional.empty(),Optional.empty(), get((long)5));
			update((long) 7, Optional.empty(),Optional.empty(), get((long)6));
			update((long) 8, Optional.empty(),Optional.empty(), get((long)7));
			update((long) 9, Optional.empty(),Optional.empty(), get((long)8));
			update((long) 10, Optional.empty(),Optional.empty(), get((long)9));
		};
	}
	
	
	public static Optional<N26Transaction> get(Long transactionId){
		return transactions.stream().filter(e -> (e.getId() == transactionId) == true).findFirst();
	}
	
	public static boolean create(Long transactionId, Double amount, String type, Optional<Long> parentId){
		N26Transaction value = new N26Transaction(transactionId, amount, type);
		parentId.ifPresent(e -> { value.setParent(get(e)); });
		return transactions.add(value);
		
	}
	
	public static Optional<N26Transaction> update(Long transactionId, Optional<Double> amount, Optional<String> type, Optional<N26Transaction> parent){     
		Optional<N26Transaction> value = get(transactionId);
			value.ifPresent(e -> {
				amount.ifPresent(x -> {e.setAmount(x);});
				type.ifPresent(y -> {e.setType(y);});
				parent.ifPresent(z -> {e.setParent(parent);}); 
			});
	     return value; 
	}
	
	public static boolean delete(Long transactionId){
		Optional<N26Transaction> value = get(transactionId);
		return value.isPresent() ?  transactions.remove(value.get()) : false;
	}
	
	public static List<Long> findAll(Optional<String> type){
		return transactions.stream()
					.filter(e -> e.getType().equalsIgnoreCase(type.get()) == true)
					.map(e -> e.getId())
					.collect(Collectors.toList());
	}
	
	//Returns the children of the following transaction
	public static void findChildren(Long transactionId, List<N26Transaction> aggregate){
		N26Transaction t = get(transactionId).get(); //Get the result
		aggregate.addAll(t.getChildren().collect(Collectors.toList()));
		t.getChildren().forEach(child ->{
			 findChildren(child.getId(),aggregate);
		 });
		//System.out.println("Aggregate::");
		//aggregate.forEach(System.out::println);
	}
	
	public static Double sum(Long transactionId){
		List<N26Transaction> aggregate = new ArrayList<N26Transaction>();
		Double sum = 0.0;
	    N26Transaction root = get(transactionId).get();
		findChildren(transactionId,aggregate);
		sum = aggregate.stream()
					   .map(t -> t.getAmount())
					   .reduce(root.getAmount(), (a,b) -> a + b);
		//System.out.println("Sum:::" + sum.floatValue());
		return sum;
	}	
	
}

/**
 * transaction_id is a long specifying a new transaction
 * amount is a double specifying the amount
 * type is a string specifying a type of the transaction.
 * parent_id is an optional long that may specify the parent transaction of this transaction.
 * 
 * @author Andre Darian Bonner
 *
 */
class N26Transaction extends Transaction{
 
    private Optional<N26Transaction> parent;
    private List<N26Transaction> children;
    
	public N26Transaction(Long id, Double amount, String type){
    	setId(id);
    	setAmount(amount);
    	setType(type);
    	parent = Optional.empty();
    	children = new ArrayList<N26Transaction>();
    }
    
	public Optional<N26Transaction> getParent() {
		return parent;
	}
	//I don't want the api to directly expose the children list
	//as i need to control it internally, but i will return a stream
	public Stream<N26Transaction> getChildren(){
		return children.stream();
	}
	
	//There are some rules that need to be applied
	public void setParent(Optional<N26Transaction> parent) {
		//Set the parent
		parent.ifPresent(newParent -> {
			//Don't allow self referencing parent
			if(this.equals(newParent)){return;}
			//Don't allow circular referencing parents
			//Don't allow any children of the current to be added as parent
			if(children.contains(newParent)){return;}
			//Remove self from current parent
			if(getParent() != null && getParent().isPresent()){ getParent().get().children.remove(this);}
			//Set the parent
			this.parent = parent;
			//Add self to new parents children
			newParent.children.add(this);
		});
	}

	@Override
    public boolean equals(Object other){
    	if (other == null) return false;
        if (other == this || ((N26Transaction)other).getId() == this.getId()) return true;
        else{ return false;}
    }
    
    @Override
    public int hashCode() {
    	return getId().hashCode();
    }

	@Override
	public String toString() {
		return "Transaction [id=" + getId() + ", amount=" + getAmount() + ", type=" + getType() + "]";
	}
    
}

