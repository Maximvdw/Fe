package org.melonbrew.fe.database;

import org.melonbrew.fe.API;
import org.melonbrew.fe.Fe;

public class Account {
	private final String name;

	private final API api;

	private final Database database;

	private double money;

	public Account(String name, Fe plugin, Database database){
		this.name = name;

		this.api = plugin.getAPI();

		this.database = database;

		this.money = -1;
	}

	public String getName(){
		return name;
	}

	public double getMoney(){
		if (database.cacheAccounts()){
			if (money != -1){
				return money;
			}

			money = database.loadAccountMoney(name);

			return money;
		}

		return database.loadAccountMoney(name);
	}

	public void withdraw(double amount){
		setMoney(getMoney() - amount);
	}

	public void deposit(double amount){
		setMoney(getMoney() + amount);
	}

	public boolean canRecieve(double amount){
		if (api.getMaxHoldings() == -1){
			return true;
		}

		return amount + getMoney() < api.getMaxHoldings();
	}

	public void setMoney(double money){
		double currentMoney = getMoney();

		if (currentMoney == money){
			return;
		}

		if (money < 0 && !api.isCurrencyNegative()){
			money = 0;
		}

		currentMoney = api.getMoneyRounded(money);

		if (api.getMaxHoldings() > 0 && currentMoney > api.getMaxHoldings()){
			currentMoney = api.getMoneyRounded(api.getMaxHoldings());
		}

		if (!database.cacheAccounts()){
			save(currentMoney);
		}else {
			this.money = currentMoney;
		}
	}

	public boolean has(double amount){
		return getMoney() >= amount;
	}

	public void save(double money){
		database.saveAccount(name, money);
	}
}
