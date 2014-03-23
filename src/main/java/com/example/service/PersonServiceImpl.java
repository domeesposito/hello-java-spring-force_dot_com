package com.example.service;

import com.force.api.ApiSession;
import com.force.api.ForceApi;
import com.force.api.QueryResult;
import com.force.sdk.oauth.context.ForceSecurityContextHolder;
import com.force.sdk.oauth.context.SecurityContext;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.model.Account;
import com.example.model.Person;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    private ForceApi getForceApi() {
        SecurityContext sc = ForceSecurityContextHolder.get();

        ApiSession s = new ApiSession();
        s.setAccessToken(sc.getSessionId());
        s.setApiEndpoint(sc.getEndPointHost());

        return new ForceApi(s);
    }

    @Override
    public void addPerson(Person person) {
        String accountId = findOrCreateAccountId(person);
        if (accountId != null)
            person.setAccountId(accountId);

        person.setAccount(null);
        getForceApi().createSObject("contact", person);
    }

    @Override
    public Person findPerson(String email) {
        if (!StringUtils.hasText(email))
            return null;
        QueryResult<Person> res = getForceApi().query("SELECT Id, FirstName, LastName, Email FROM contact WHERE Email = '" + email + "'", Person.class);
        return res.getTotalSize() > 0 ? res.getRecords().get(0) : null;
    }

    @Override
    public List<Person> listPeople() {
        QueryResult<Person> res = getForceApi().query("SELECT Id, FirstName, LastName, Email, Account.Id, Account.Name FROM contact", Person.class);
        return res.getRecords();
    }

    @Override
    public void removePerson(String id) {
        getForceApi().deleteSObject("contact", id);
    }

    private String getAccountId(String accountName) {
        if (!StringUtils.hasText(accountName))
            return null;
        accountName = StringUtils.quote(accountName.replace("'", "\\'"));
        QueryResult<Account> res = getForceApi().query("SELECT Id, Name FROM account WHERE Name = " + accountName , Account.class);
        return res.getTotalSize() > 0 ? res.getRecords().get(0).getId() : null;
    }

    private String addAccount(Account account) {
        return getForceApi().createSObject("account", account);
    }

    private String findOrCreateAccountId(Person person) {
        String accountId = null;
        String accountName = person.getAccount().getName();
        if (StringUtils.hasText(accountName)) {
            accountId = getAccountId(accountName);
            if (accountId == null) {
                accountId = addAccount(person.getAccount());
            }
        }
        return accountId;
    }

}
