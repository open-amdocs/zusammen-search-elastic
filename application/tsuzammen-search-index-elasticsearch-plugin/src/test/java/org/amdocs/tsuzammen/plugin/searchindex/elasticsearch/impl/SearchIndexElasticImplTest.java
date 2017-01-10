/*
 * 				Copyright takedown notice
 *
 * If you believe your copyright protected work was posted on Amdocs account in Github without authorization,
 * you may submit a copyright infringement notification. Before doing so, please consider whether fair use,
 * fair dealing, or a similar exception to copyright applies. These requests should only be submitted by the
 * copyright owner or an agent authorized to act on the owner’s behalf.
 *
 * Please bear in mind that requesting the removal of content by submitting an infringement notification means
 * initiating a legal process.
 *
 * Do not make false claims. Misuse of this process may result legal consequences.
 *
 * You can submit an alleged copyright infringement by sending an email to amdocsfossfp@amdocs.com and specifying
 * the following information (copyright takedown notifications must include the following elements.
 * Without this information, we will be unable to take action on your request):
 *
 * 1. Your contact information
 * 	You’ll need to provide information that will allow us to contact you regarding your complaint, such as an email address, physical address or telephone number.
 *
 * 2. A description of your work that you believe has been infringed
 * 	In your complaint, please describe the copyrighted content you want to protect.
 *
 * 3. You must agree to and include the following statement:
 * 	“I believe that the use of the material is not authorized by the copyright owner, its agent, or the law.”
 *
 * 4. And the following statement:
 * 	"The information in this notification is accurate, and I am the owner, or an agent authorized to act on behalf of the owner”
 *
 * 5. Your signature
 * 	Please make sure to sign at the bottom of your complaint.
 *
 */

package org.amdocs.tsuzammen.plugin.searchindex.elasticsearch.impl;

import org.amdocs.tsuzammen.datatypes.Id;
import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.datatypes.searchindex.SearchIndexContext;
import org.amdocs.tsuzammen.datatypes.searchindex.SearchIndexSpace;
import org.amdocs.tsuzammen.datatypes.searchindex.SearchResult;
import org.amdocs.tsuzammen.plugin.searchindex.elasticsearch.EsTestUtils;
import org.amdocs.tsuzammen.plugin.searchindex.elasticsearch.datatypes.EsSearchCriteria;
import org.amdocs.tsuzammen.plugin.searchindex.elasticsearch.datatypes.EsSearchResult;
import org.amdocs.tsuzammen.plugin.searchindex.elasticsearch.datatypes.EsSearchableData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class SearchIndexElasticImplTest {
  private Id searchableId = null;
  private String type;
  private String tenant;
  private String user;
  private String createName;
  private String updateName;

  private void initSearchData() {
    tenant = "searchindexelasticimpltest";
    type = "type1";
    user = "user";
    createName = "createName";
    updateName = "updateName";
    if (Objects.isNull(searchableId)) {
      searchableId = new Id();
    }
  }

  @Test(groups = "create")
  public void testCreate() {
    initSearchData();

    String message = "create es data test";
    List<String> tags = new ArrayList<>();
    tags.add("a");
    tags.add("b");
    tags.add("c");

    SessionContext sessionContext = EsTestUtils.createSessionContext(tenant, user);
    SearchIndexContext searchContext =
        EsTestUtils.createSearchIndexContext(SearchIndexSpace.PUBLIC);
    EsSearchableData searchableData =
        EsTestUtils.createSearchableData(type, createName, message, tags);
    new SearchIndexElasticImpl()
        .create(sessionContext, searchContext, searchableData, searchableId);
  }

  @Test(expectedExceptions = {RuntimeException.class},
      expectedExceptionsMessageRegExp = "Invalid instance of SearchableData, EsSearchableData "
          + "object is expected")
  public void testCreateInvalidSearchableDataInstance() {
    initSearchData();
    SessionContext sessionContext = EsTestUtils.createSessionContext(tenant, user);
    SearchIndexContext searchContext =
        EsTestUtils.createSearchIndexContext(SearchIndexSpace.PUBLIC);
    new SearchIndexElasticImpl()
        .create(sessionContext, searchContext, null, searchableId);
  }

  @Test(groups = "create", dependsOnMethods = {"testCreate"})
  public void testUpdate() {
    initSearchData();
    List<String> tags = new ArrayList<>();
    tags.add("a");
    tags.add("b");

    SessionContext sessionContext = EsTestUtils.createSessionContext(tenant, user);
    SearchIndexContext searchContext =
        EsTestUtils.createSearchIndexContext(SearchIndexSpace.PUBLIC);
    EsSearchableData searchableData =
        EsTestUtils.createSearchableData(type, updateName, null, tags);
    new SearchIndexElasticImpl()
        .update(sessionContext, searchContext, searchableData, searchableId);

  }

  @Test(groups = "create", expectedExceptions = {RuntimeException.class},
      expectedExceptionsMessageRegExp = ".*Searchable data for tenant - " +
          "'searchindexelasticimpltest', type - 'type1' and id - .* was not found.")
  public void testUpdateIdNotExist() {
    initSearchData();
    List<String> tags = new ArrayList<>();
    tags.add("a");
    tags.add("b");

    SessionContext sessionContext = EsTestUtils.createSessionContext(tenant, user);
    SearchIndexContext searchContext =
        EsTestUtils.createSearchIndexContext(SearchIndexSpace.PUBLIC);
    EsSearchableData searchableData =
        EsTestUtils.createSearchableData(type, updateName, null, tags);
    new SearchIndexElasticImpl()
        .update(sessionContext, searchContext, searchableData, new Id());

  }

  @Test(groups = "create", expectedExceptions = {RuntimeException.class},
      expectedExceptionsMessageRegExp = ".*Searchable data for tenant - 'invalidIndex' was not found.")
  public void testUpdateIndexNotExist() {
    initSearchData();
    List<String> tags = new ArrayList<>();
    tags.add("a");
    tags.add("b");

    SessionContext sessionContext = EsTestUtils.createSessionContext("invalidIndex", user);
    SearchIndexContext searchContext =
        EsTestUtils.createSearchIndexContext(SearchIndexSpace.PUBLIC);
    EsSearchableData searchableData =
        EsTestUtils.createSearchableData(type, updateName, null, tags);
    new SearchIndexElasticImpl()
        .update(sessionContext, searchContext, searchableData, new Id());

  }

  @Test(groups = "create", dependsOnMethods = {"testCreate"}, expectedExceptions =
      {RuntimeException.class}, expectedExceptionsMessageRegExp = ".*Searchable data for tenant - "
      + "'searchindexelasticimpltest', type - 'invalidType' and id - .* was not found.")
  public void testUpdateTypeNotExist() {
    initSearchData();
    List<String> tags = new ArrayList<>();
    tags.add("a");
    tags.add("b");

    SessionContext sessionContext = EsTestUtils.createSessionContext(tenant, user);
    SearchIndexContext searchContext =
        EsTestUtils.createSearchIndexContext(SearchIndexSpace.PUBLIC);
    EsSearchableData searchableData =
        EsTestUtils.createSearchableData("invalidType", updateName, null, tags);
    new SearchIndexElasticImpl()
        .update(sessionContext, searchContext, searchableData, searchableId);

  }

  @Test(groups = "search", dependsOnGroups = {"create"})
  public void testSearch() {
    initSearchData();
    SessionContext sessionContext = EsTestUtils.createSessionContext(tenant, user);
    SearchIndexContext searchContext =
        EsTestUtils.createSearchIndexContext(SearchIndexSpace.PUBLIC);

    Optional<String> json = EsTestUtils.getJson(updateName, null, null);
    Assert.assertEquals(json.isPresent(), true);
    if (json.isPresent()) {
      String jsonQuery = EsTestUtils.wrapperTermQuery(json.get().toLowerCase());

      List<String> types = new ArrayList<>();
      types.add(type);
      EsSearchCriteria searchCriteria = EsTestUtils.createSearchCriteria(types, 0, 1, jsonQuery);
      SearchResult searchResult =
          new SearchIndexElasticImpl().search(sessionContext, searchContext, searchCriteria);
      Assert.assertEquals(searchResult instanceof EsSearchResult, true);
      if (searchResult instanceof EsSearchResult) {
        Assert
            .assertEquals(
                ((EsSearchResult) searchResult).getSearchResponse().getHits().getTotalHits(),
                1);
        Assert.assertEquals(
            ((EsSearchResult) searchResult).getSearchResponse().getHits().getHits().length, 1);
      }
    }
  }


  @Test(dependsOnGroups = {"create", "search"})
  public void testDelete() {
    initSearchData();

    SessionContext sessionContext = EsTestUtils.createSessionContext(tenant, user);
    EsSearchableData searchableData =
        EsTestUtils.createSearchableData(type, null, null, null);
    new SearchIndexElasticImpl()
        .delete(sessionContext, null, searchableData, searchableId);
  }

  @Test(dependsOnGroups = {"create", "search"}, expectedExceptions =
      {RuntimeException.class},
      expectedExceptionsMessageRegExp = "Delete failed - Searchable data for tenant - "
          + "'searchindexelasticimpltest', type - 'type1' and id - .* was not found.")
  public void testDeleteNotFound() {
    initSearchData();

    SessionContext sessionContext = EsTestUtils.createSessionContext(tenant, user);
    EsSearchableData searchableData =
        EsTestUtils.createSearchableData(type, null, null, null);
    new SearchIndexElasticImpl()
        .delete(sessionContext, null, searchableData, new Id());
  }

}