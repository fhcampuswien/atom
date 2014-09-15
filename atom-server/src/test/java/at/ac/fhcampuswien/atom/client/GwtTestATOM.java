/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client;

import java.util.List;

import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainObjectList;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.rpc.AtomService;
import at.ac.fhcampuswien.atom.shared.rpc.AtomServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * GWT JUnit <b>integration</b> tests must extend GWTTestCase.
 * Using <code>"GwtTest*"</code> naming pattern exclude them from running with
 * surefire during the test phase.
 */
public class GwtTestATOM extends GWTTestCase {


	private static final String domainObjectClassName = "at.ac.fhcampuswien.atom.shared.domain.DomainObject";
	private String authBackDoor = "!<>! Very Special Back D00r = A Way Around The Auth Mechanism !<>!";
	
  /**
   * Must refer to a valid module that sources this class.
   */
  public String getModuleName() {
    return "at.ac.fhcampuswien.atom.ATOMJUnit";
  }

//  /**
//   * Tests the FieldVerifier.
//   */
//  public void testFieldVerifier() {
//    assertFalse(FieldVerifier.isValidName(null));
//    assertFalse(FieldVerifier.isValidName(""));
//    assertFalse(FieldVerifier.isValidName("a"));
//    assertFalse(FieldVerifier.isValidName("ab"));
//    assertFalse(FieldVerifier.isValidName("abc"));
//    assertTrue(FieldVerifier.isValidName("abcd"));
//  }

  /**
   * This test will send a request to the server using the greetServer method in
   * GreetingService and verify the response.
   */
  public void testGreetingService() {
    // Create the service that we will test.
    AtomServiceAsync atomService = GWT.create(AtomService.class);
    ServiceDefTarget target = (ServiceDefTarget) atomService;
    target.setServiceEntryPoint(GWT.getModuleBaseURL() + "ATOM/rpc");

    // Since RPC calls are asynchronous, we will need to wait for a response
    // after this test method returns. This line tells the test runner to wait
    // up to 10 seconds before timing out.
    delayTestFinish(10000);

    // Send a request to the server.
//		atomService.greetServer("GWT User", new AsyncCallback<String>() {
//			public void onFailure(Throwable caught) {
//				// The request resulted in an unexpected error.
//				fail("Request failure: " + caught.getMessage());
//			}
//
//			public void onSuccess(String result) {
//				// Verify that the response is correct.
//				assertTrue(result.startsWith("Hello, GWT User!"));
//
//				// Now that we have received a response, we need to tell the
//				// test runner
//				// that the test is complete. You must call finishTest() after
//				// an
//				// asynchronous test finishes successfully, or the test will
//				// time out.
//				finishTest();
//			}
//		});
  }

  public void testGetDomainTree() {
		// Create the service that we will test.
		AtomServiceAsync atomService = GWT.create(AtomService.class);
		ServiceDefTarget target = (ServiceDefTarget) atomService;
		target.setServiceEntryPoint(GWT.getModuleBaseURL() + "ATOM/rpc");

		// Since RPC calls are asynchronous, we will need to wait for a response
		// after this test method returns. This line tells the test runner to
		// wait
		// up to 10 seconds before timing out.
		delayTestFinish(Integer.MAX_VALUE);

		atomService.getDomainTree(authBackDoor, new AsyncCallback<DomainClass>() {

			public void onFailure(Throwable caught) {
				fail("Request failure: " + caught.getMessage());
			}

			public void onSuccess(DomainClass result) {
				assertEquals(result.getName(), domainObjectClassName);
				finishTest();
			}
		});
	}

	public void testGetListOfDomainObject() {
		// Create the service that we will test.
		AtomServiceAsync atomService = GWT.create(AtomService.class);
		ServiceDefTarget target = (ServiceDefTarget) atomService;
		target.setServiceEntryPoint(GWT.getModuleBaseURL() + "ATOM/rpc");

		delayTestFinish(Integer.MAX_VALUE);
		final int pageSize = 20;

		atomService.getListOfDomainObject(authBackDoor, domainObjectClassName, 0,
				pageSize, null, null, null, false, false,
				new AsyncCallback<DomainObjectList>() {

					public void onSuccess(DomainObjectList result) {

						assertNotNull(result);

						assertEquals(domainObjectClassName,
								result.getDomainClassName());

						List<DomainObject> dos = result.getDomainObjects();

						if (dos != null) {
							assertTrue(dos.size() <= pageSize);
//							assertTrue(dos.size() > 0);
						}

						finishTest();
					}

					public void onFailure(Throwable caught) {
						fail("Request failure: " + caught.getMessage());
					}
				});
	}

}
