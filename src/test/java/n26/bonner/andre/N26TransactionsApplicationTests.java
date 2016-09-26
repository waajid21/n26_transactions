package n26.bonner.andre;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class N26TransactionsApplicationTests {

	@Before
	public void loadTransactions(){
		N26TransactionsApplication.create((long) 1, 45.0, "credit", Optional.empty());
		N26TransactionsApplication.create((long) 2, 1000.0, "credit", Optional.empty());
		N26TransactionsApplication.create((long) 3, -2500.0, "debit", Optional.empty());
		N26TransactionsApplication.create((long) 4, 525.0, "credit", Optional.empty());
		N26TransactionsApplication.create((long) 5, -375.0, "debit", Optional.empty());
		N26TransactionsApplication.create((long) 6, 34.57, "credit", Optional.empty());
		N26TransactionsApplication.create((long) 7, 34.57, "credit", Optional.empty());
		N26TransactionsApplication.create((long) 8, -634.57, "debit", Optional.empty());
		N26TransactionsApplication.create((long) 9, 34.57, "credit", Optional.empty());
		N26TransactionsApplication.create((long) 10, -57.17, "debit", Optional.empty());
	}
	
	@Before
	public void setChildren(){
		N26TransactionsApplication.update((long) 1, Optional.empty(),Optional.empty(), N26TransactionsApplication.get((long)5));
		N26TransactionsApplication.update((long) 2, Optional.empty(),Optional.empty(), N26TransactionsApplication.get((long)5));
		N26TransactionsApplication.update((long) 3, Optional.empty(),Optional.empty(), Optional.empty());
		N26TransactionsApplication.update((long) 4, Optional.empty(),Optional.empty(), N26TransactionsApplication.get((long)4));
		N26TransactionsApplication.update((long) 5, Optional.empty(),Optional.empty(), N26TransactionsApplication.get((long)6));
		N26TransactionsApplication.update((long) 6, Optional.empty(),Optional.empty(), N26TransactionsApplication.get((long)5));
		N26TransactionsApplication.update((long) 7, Optional.empty(),Optional.empty(), N26TransactionsApplication.get((long)6));
		N26TransactionsApplication.update((long) 8, Optional.empty(),Optional.empty(), N26TransactionsApplication.get((long)7));
		N26TransactionsApplication.update((long) 9, Optional.empty(),Optional.empty(), N26TransactionsApplication.get((long)8));
		N26TransactionsApplication.update((long) 10, Optional.empty(),Optional.empty(), N26TransactionsApplication.get((long)9));
	}
	
	@After
	public void clearTransactions(){
		N26TransactionsApplication.transactions.clear();
	}
	
	@Test
	public void put() {
		N26TransactionsApplication.create((long)11, 67.0, "credit", Optional.of((long)3));
		assertTrue(N26TransactionsApplication.transactions.size() == 11);	
	}
	
	@Test
	public void get() {
		Optional<N26Transaction> t = N26TransactionsApplication.get((long)3);
		assertTrue(t.isPresent());
		assertTrue(t.get().getAmount() == -2500.0);
		assertTrue(t.get().getType() == "debit");
		assertTrue(t.get().getParent().equals(Optional.empty()));
	}
	
	@Test
	public void parent() {
		N26TransactionsApplication.create((long)11, 67.0, "credit", Optional.of((long)3));
		assertTrue(N26TransactionsApplication.transactions.size() == 11);
		assertTrue(N26TransactionsApplication.get((long)11).get().getParent().equals(N26TransactionsApplication.get((long)3)));
	}
	
	@Test
	public void findAll() {
		List<Long> all = N26TransactionsApplication.findAll(Optional.of("credit"));
		assertTrue(all.size() == 6);	
	}
	
	@Test
	public void noChildren() {
		List<N26Transaction> children = new ArrayList<N26Transaction>();
		N26TransactionsApplication.findChildren((long)3,children);
		assertTrue(children.size() == 0);		
	}

	@Test
	public void someChildren() {
		List<N26Transaction> children = new ArrayList<N26Transaction>();
		N26TransactionsApplication.findChildren((long)5,children);
		assertTrue(children.size() == 2);	
	}
	
	@Test
	public void grandChildren() {
		List<N26Transaction> children = new ArrayList<N26Transaction>();
		N26TransactionsApplication.findChildren((long)6,children);
		assertTrue(children.size() == 7);		
	}
	
	@Test
	public void sum() {
		Double sum = N26TransactionsApplication.sum((long)6);
		assertTrue(sum.floatValue() == 81.97f);		
	}
	
	@Test
	public void sumNoChildren() {
		Double sum = N26TransactionsApplication.sum((long)3);
		assertTrue(sum.floatValue() == -2500f);		
	}
	
	@Test
	public void sum2Children() {
		Double sum = N26TransactionsApplication.sum((long)5);
		assertTrue(sum.floatValue() == 670.0f);		
	}
	
}
