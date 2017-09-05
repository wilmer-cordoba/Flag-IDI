package util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.EdmMetadataRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetIteratorRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientEntitySetIterator;
import org.apache.olingo.client.api.domain.ClientEnumValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;

public class Consume {

	private ODataClient client;
	private Edm edm;

	public Consume() {
		print("\n----- Read Edm ------------------------------");
		client = ODataClientFactory.getClient();
		try {
			Edm edm = readEdm("http://services.odata.org/V4/Northwind/Northwind.svc");
			List<FullQualifiedName> ctFqns = new ArrayList<FullQualifiedName>();
			List<FullQualifiedName> etFqns = new ArrayList<FullQualifiedName>();
			for (EdmSchema schema : edm.getSchemas()) {
				for (EdmComplexType complexType : schema.getComplexTypes()) {
					ctFqns.add(complexType.getFullQualifiedName());
				}
				for (EdmEntityType entityType : schema.getEntityTypes()) {
					etFqns.add(entityType.getFullQualifiedName());
				}
			}
			print("Found ComplexTypes", ctFqns);
			print("Found EntityTypes", etFqns);

			print("\n----- Inspect each property and its type of the first entity: " + etFqns.get(0) + "----");
			EdmEntityType etype = edm.getEntityType(etFqns.get(0));
			for (String propertyName : etype.getPropertyNames()) {
				EdmProperty property = etype.getStructuralProperty(propertyName);
				FullQualifiedName typeName = property.getType().getFullQualifiedName();
				print("property '" + propertyName + "' " + typeName);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Edm readEdm(String serviceUrl) throws IOException {
		EdmMetadataRequest request = client.getRetrieveRequestFactory().getMetadataRequest(serviceUrl);
		ODataRetrieveResponse<Edm> response = request.execute();
		return response.getBody();
	}

	private static void print(String content) {
		System.out.println(content);
	}

	private static void print(String content, List<?> list) {
		System.out.println(content);
		for (Object o : list) {
			System.out.println("    " + o);
		}
		System.out.println();
	}
}
