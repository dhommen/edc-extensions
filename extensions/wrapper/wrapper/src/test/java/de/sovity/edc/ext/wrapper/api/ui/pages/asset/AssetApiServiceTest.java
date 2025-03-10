/*
 *  Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */
package de.sovity.edc.ext.wrapper.api.ui.pages.asset;


import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.UiAsset;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiAssetEditMetadataRequest;
import de.sovity.edc.ext.wrapper.TestUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.EdcPropertyUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.FailedMappingException;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
public class AssetApiServiceTest {

    public static final String DATA_SINK = "http://my-data-sink/api/stuff";
    EdcClient client;
    EdcPropertyUtils edcPropertyUtils;

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
        edcPropertyUtils = new EdcPropertyUtils();
        client = TestUtils.edcClient();
    }

    @Test
    void assetPage(AssetService assetStore) {
        // arrange
        var properties = Map.of(
                Asset.PROPERTY_ID, "asset-1",
                Prop.Dcat.LANDING_PAGE, "https://data-source.my-org/docs"
        );
        createAsset(assetStore, "2023-06-01", properties);

        // act
        var result = client.uiApi().getAssetPage();

        // assert
        var assets = result.getAssets();
        assertThat(assets).hasSize(1);
        var asset = assets.get(0);
        assertThat(asset.getAssetId()).isEqualTo(properties.get(Asset.PROPERTY_ID));
        assertThat(asset.getLandingPageUrl()).isEqualTo(properties.get(Prop.Dcat.LANDING_PAGE));
    }

    @Test
    void assetPageSorting(AssetService assetService) {
        // arrange
        createAsset(assetService, "2023-06-01", Map.of(Asset.PROPERTY_ID, "asset-1"));
        createAsset(assetService, "2023-06-03", Map.of(Asset.PROPERTY_ID, "asset-3"));
        createAsset(assetService, "2023-06-02", Map.of(Asset.PROPERTY_ID, "asset-2"));

        // act
        var result = client.uiApi().getAssetPage();

        // assert
        assertThat(result.getAssets())
                .extracting(UiAsset::getAssetId)
                .containsExactly("asset-3", "asset-2", "asset-1");
    }

    @Test
    void testAssetCreation(AssetService assetService) {
        // arrange
        var dataAddressProperties = Map.of(
                Prop.Edc.TYPE, "HttpData",
                Prop.Edc.BASE_URL, DATA_SINK,
                Prop.Edc.PROXY_METHOD, "true",
                Prop.Edc.PROXY_PATH, "true",
                Prop.Edc.PROXY_QUERY_PARAMS, "true",
                Prop.Edc.PROXY_BODY, "true",

                // tests that a property without a context URL will survive the JSON-LD mapping
                "oauth2:tokenUrl", "https://token-url"
        );
        var uiAssetRequest = UiAssetCreateRequest.builder()
                .id("asset-1")
                .title("AssetTitle")
                .description("AssetDescription")
                .licenseUrl("https://license-url")
                .version("1.0.0")
                .language("en")
                .mediaType("application/json")
                .dataCategory("dataCategory")
                .dataSubcategory("dataSubcategory")
                .dataModel("dataModel")
                .geoReferenceMethod("geoReferenceMethod")
                .transportMode("transportMode")
                .keywords(List.of("keyword1", "keyword2"))
                .publisherHomepage("publisherHomepage")
                .dataAddressProperties(dataAddressProperties)
                .build();

        // act
        var response = client.uiApi().createAsset(uiAssetRequest);

        // assert
        assertThat(response.getId()).isEqualTo("asset-1");

        var assets = client.uiApi().getAssetPage().getAssets();
        assertThat(assets).hasSize(1);
        var asset = assets.get(0);
        assertThat(asset.getAssetId()).isEqualTo("asset-1");
        assertThat(asset.getTitle()).isEqualTo("AssetTitle");
        assertThat(asset.getDescription()).isEqualTo("AssetDescription");
        assertThat(asset.getVersion()).isEqualTo("1.0.0");
        assertThat(asset.getLanguage()).isEqualTo("en");
        assertThat(asset.getMediaType()).isEqualTo("application/json");
        assertThat(asset.getDataCategory()).isEqualTo("dataCategory");
        assertThat(asset.getDataSubcategory()).isEqualTo("dataSubcategory");
        assertThat(asset.getDataModel()).isEqualTo("dataModel");
        assertThat(asset.getGeoReferenceMethod()).isEqualTo("geoReferenceMethod");
        assertThat(asset.getTransportMode()).isEqualTo("transportMode");
        assertThat(asset.getLicenseUrl()).isEqualTo("https://license-url");
        assertThat(asset.getKeywords()).isEqualTo(List.of("keyword1", "keyword2"));
        assertThat(asset.getCreatorOrganizationName()).isEqualTo("My Org");
        assertThat(asset.getPublisherHomepage()).isEqualTo("publisherHomepage");
        assertThat(asset.getHttpDatasourceHintsProxyMethod()).isTrue();
        assertThat(asset.getHttpDatasourceHintsProxyPath()).isTrue();
        assertThat(asset.getHttpDatasourceHintsProxyQueryParams()).isTrue();
        assertThat(asset.getHttpDatasourceHintsProxyBody()).isTrue();

        var assetWithDataAddress = assetService.query(QuerySpec.max()).orElseThrow(FailedMappingException::ofFailure).toList().get(0);
        assertThat(assetWithDataAddress.getDataAddress().getProperties()).isEqualTo(dataAddressProperties);
    }

    @Test
    void testEditAssetMetadata(AssetService assetService) {
        // arrange
        var dataAddress = Map.of(
                Prop.Edc.TYPE, "HttpData",
                Prop.Edc.BASE_URL, DATA_SINK,
                Prop.Edc.PROXY_METHOD, "true",
                Prop.Edc.PROXY_PATH, "true",
                Prop.Edc.PROXY_QUERY_PARAMS, "true",
                Prop.Edc.PROXY_BODY, "true",
                "oauth2:tokenUrl", "https://token-url"
        );
        var createRequest = UiAssetCreateRequest.builder()
                .id("asset-1")
                .title("AssetTitle")
                .description("AssetDescription")
                .licenseUrl("https://license-url")
                .version("1.0.0")
                .language("en")
                .mediaType("application/json")
                .dataCategory("dataCategory")
                .dataSubcategory("dataSubcategory")
                .dataModel("dataModel")
                .geoReferenceMethod("geoReferenceMethod")
                .transportMode("transportMode")
                .keywords(List.of("keyword1", "keyword2"))
                .publisherHomepage("publisherHomepage")
                .dataAddressProperties(dataAddress)
                .build();
        client.uiApi().createAsset(createRequest);
        var editRequest = UiAssetEditMetadataRequest.builder()
                .title("AssetTitle 2")
                .description("AssetDescription 2")
                .licenseUrl("https://license-url/2")
                .version("2.0.0")
                .language("de")
                .mediaType("application/json+utf8")
                .dataCategory("dataCategory2")
                .dataSubcategory("dataSubcategory2")
                .dataModel("dataModel2")
                .geoReferenceMethod("geoReferenceMethod2")
                .transportMode("transportMode2")
                .keywords(List.of("keyword3"))
                .publisherHomepage("publisherHomepage2")
                .build();

        // act
        var response = client.uiApi().editAssetMetadata("asset-1", editRequest);

        // assert
        assertThat(response.getId()).isEqualTo("asset-1");

        var assets = client.uiApi().getAssetPage().getAssets();
        assertThat(assets).hasSize(1);
        var asset = assets.get(0);
        assertThat(asset.getAssetId()).isEqualTo("asset-1");
        assertThat(asset.getTitle()).isEqualTo("AssetTitle 2");
        assertThat(asset.getDescription()).isEqualTo("AssetDescription 2");
        assertThat(asset.getVersion()).isEqualTo("2.0.0");
        assertThat(asset.getLanguage()).isEqualTo("de");
        assertThat(asset.getMediaType()).isEqualTo("application/json+utf8");
        assertThat(asset.getDataCategory()).isEqualTo("dataCategory2");
        assertThat(asset.getDataSubcategory()).isEqualTo("dataSubcategory2");
        assertThat(asset.getDataModel()).isEqualTo("dataModel2");
        assertThat(asset.getGeoReferenceMethod()).isEqualTo("geoReferenceMethod2");
        assertThat(asset.getTransportMode()).isEqualTo("transportMode2");
        assertThat(asset.getLicenseUrl()).isEqualTo("https://license-url/2");
        assertThat(asset.getKeywords()).isEqualTo(List.of("keyword3"));
        assertThat(asset.getCreatorOrganizationName()).isEqualTo("My Org");
        assertThat(asset.getPublisherHomepage()).isEqualTo("publisherHomepage2");
        assertThat(asset.getHttpDatasourceHintsProxyMethod()).isTrue();
        assertThat(asset.getHttpDatasourceHintsProxyPath()).isTrue();
        assertThat(asset.getHttpDatasourceHintsProxyQueryParams()).isTrue();
        assertThat(asset.getHttpDatasourceHintsProxyBody()).isTrue();

        var assetWithDataAddress = assetService.query(QuerySpec.max()).orElseThrow(FailedMappingException::ofFailure).toList().get(0);
        assertThat(assetWithDataAddress.getDataAddress().getProperties()).isEqualTo(dataAddress);
    }

    @Test
    void testAssetCreation_noProxying() {
        // arrange
        var dataAddressProperties = Map.of(
                Prop.Edc.TYPE, "HttpData",
                Prop.Edc.BASE_URL, DATA_SINK
        );
        var uiAssetRequest = UiAssetCreateRequest.builder()
                .id("asset-1")
                .dataAddressProperties(dataAddressProperties)
                .build();

        // act
        var response = client.uiApi().createAsset(uiAssetRequest);

        // assert
        assertThat(response.getId()).isEqualTo("asset-1");
        var assets = client.uiApi().getAssetPage().getAssets();
        assertThat(assets).hasSize(1);
        var asset = assets.get(0);
        assertThat(asset.getHttpDatasourceHintsProxyMethod()).isFalse();
        assertThat(asset.getHttpDatasourceHintsProxyPath()).isFalse();
        assertThat(asset.getHttpDatasourceHintsProxyQueryParams()).isFalse();
        assertThat(asset.getHttpDatasourceHintsProxyBody()).isFalse();
    }

    @Test
    void testAssetCreation_differentDataAddressType() {
        // arrange
        var dataAddressProperties = Map.of(
                Prop.Edc.TYPE, "Unknown"
        );
        var uiAssetRequest = UiAssetCreateRequest.builder()
                .id("asset-1")
                .dataAddressProperties(dataAddressProperties)
                .build();

        // act
        var response = client.uiApi().createAsset(uiAssetRequest);

        // assert
        assertThat(response.getId()).isEqualTo("asset-1");
        var assets = client.uiApi().getAssetPage().getAssets();
        assertThat(assets).hasSize(1);
        var asset = assets.get(0);
        assertThat(asset.getHttpDatasourceHintsProxyMethod()).isNull();
        assertThat(asset.getHttpDatasourceHintsProxyPath()).isNull();
        assertThat(asset.getHttpDatasourceHintsProxyQueryParams()).isNull();
        assertThat(asset.getHttpDatasourceHintsProxyBody()).isNull();
    }

    @Test
    void testDeleteAsset(AssetService assetService) {
        // arrange
        createAsset(assetService, "2023-06-01", Map.of(Asset.PROPERTY_ID, "asset-1"));
        assertThat(assetService.query(QuerySpec.max()).getContent()).isNotEmpty();

        // act
        var response = client.uiApi().deleteAsset("asset-1");

        // assert
        assertThat(response.getId()).isEqualTo("asset-1");
        assertThat(assetService.query(QuerySpec.max()).getContent()).isEmpty();
    }

    private void createAsset(
            AssetService assetService,
            String date,
            Map<String, String> properties
    ) {
        DataAddress dataAddress = DataAddress.Builder.newInstance()
                .type("HttpData")
                .property(Prop.Edc.BASE_URL, DATA_SINK)
                .build();

        var asset = Asset.Builder.newInstance()
                .id(properties.get(Asset.PROPERTY_ID))
                .properties(edcPropertyUtils.toMapOfObject(properties))
                .dataAddress(dataAddress)
                .createdAt(dateFormatterToLong(date))
                .build();
        assetService.create(asset);
    }

    @SneakyThrows
    private static long dateFormatterToLong(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.parse(date).getTime();
    }
}

