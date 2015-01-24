package org.kotemaru.tool.ledger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Ledgers {
	Map<String,Ledger> ledgerMap = new HashMap<String,Ledger>();
	
	public void add(Ledger ledger) {
		ledgerMap.put(ledger.sheetName, ledger);
	}
	public Collection<Ledger> values() {
		return ledgerMap.values();
	}

	public Ledger get(String name) {
		Ledger ledger = ledgerMap.get(name);
		if (ledger == null) {
			throw new Error("Unknown ledger "+name+" in "+ledgerMap.keySet());
		}
		return ledger;
	}
	public Ledger getWithNull(String name) {
		return ledgerMap.get(name);
	}

}
