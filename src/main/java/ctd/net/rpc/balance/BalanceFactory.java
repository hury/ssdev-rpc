package ctd.net.rpc.balance;

public class BalanceFactory {
	public static Balance getBalance(){
		return new RandomBalance();
	}
}
