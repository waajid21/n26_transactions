package n26.bonner.andre;

/**
 * transaction_id is a long specifying a new transaction
 * amount is a double specifying the amount
 * type is a string specifying a type of the transaction.
 * parent_id is an optional long that may specify the parent transaction of this transaction.
 * 
 * @author Andre Darian Bonner
 *
 */
public abstract class Transaction {

	private Long id;
	private Double amount;
	private String type;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
