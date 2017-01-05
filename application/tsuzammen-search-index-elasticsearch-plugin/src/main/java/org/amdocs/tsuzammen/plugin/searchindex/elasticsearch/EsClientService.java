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

package org.amdocs.tsuzammen.plugin.searchindex.elasticsearch;


import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.utils.common.CommonMethods;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class EsClientService {

  public TransportClient start(SessionContext sessionContext, EsConfig config) {
    TransportClient client;
    String host = config.getHost();
    String clusterName = config.getClusterName();
    int transportPort = config.getTransportPort();

    if (CommonMethods.isEmpty(host)) {
      throw new RuntimeException("Elastic Search 'host' in configuration file is empty");
    }
    if (CommonMethods.isEmpty(clusterName)) {
      throw new RuntimeException("Elastic Search 'cluster name' in configuration file is empty");
    }

    try {
      Settings settings = Settings.builder().put("cluster.name", clusterName).build();
      client = new PreBuiltTransportClient(settings).addTransportAddress(
          new InetSocketTransportAddress(InetAddress.getByName(host),
              transportPort));
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
    return client;
  }

  public void stop(SessionContext sessionContext, TransportClient client) {
    if (Objects.nonNull(client)) {
      client.close();
    }
  }


}
