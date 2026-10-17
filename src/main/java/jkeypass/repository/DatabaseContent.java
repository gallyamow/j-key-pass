package jkeypass.repository;

import jkeypass.crypto.CryptoService;
import jkeypass.domain.Account;

import java.util.List;

public record DatabaseContent(List<Account> accounts, CryptoService crypto) {
}
