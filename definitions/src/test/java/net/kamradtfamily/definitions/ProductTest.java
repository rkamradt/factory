/*
 * Copyright 2014 Randal Kamradt Sr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.kamradtfamily.definitions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Randal Kamradt Sr
 */
public class ProductTest {
    static EntityManagerFactory emf;

    EntityManager em;
    EntityTransaction trans;
    
    public ProductTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws IOException {
        Properties props = new Properties();
        props.load(ProductTest.class.getResourceAsStream("/database.properties"));
        emf = Persistence.createEntityManagerFactory("net.kamradtfamily.factory_definitions_jar_1.0-SNAPSHOTPU", props);
    }
    
    @AfterClass
    public static void tearDownClass() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        deleteTestData(em);
        trans.commit();
        em.close();

        emf.close();
    }
    
    @Before
    public void setUp() {
        em = emf.createEntityManager();
        trans = em.getTransaction();
        trans.begin();
    }
    
    @After
    public void tearDown() {
        if(trans != null && trans.isActive()) {
            trans.commit();
        }
        if(em != null && em.isOpen()) {
            em.close();
        }
    }

    /**
     * Test of getName method, of class Product.
     */
    @org.junit.Test
    public void testCreateProduct() {
        System.out.println("testCreateProduct");
        Product product = new Product();
        product.setName("product");
        em.persist(product);
        Worker worker = new Worker();
        worker.setRole("role");
        em.persist(worker);
        MaterialSource materialSource = new MaterialSource();
        materialSource.setAddress(new Address());
        materialSource.getAddress().setCity("Springfield");
        materialSource.getAddress().setStreet1("1234 Main St");
        materialSource.getAddress().setState("OR");
        materialSource.getAddress().setZip("12345");
        materialSource.setName("Lowes");
        em.persist(materialSource.getAddress());
        em.persist(materialSource);
        Material wood = new Material();
        wood.setName("wood");
        wood.setSource(materialSource);
        em.persist(wood);
        Material cloth = new Material();
        cloth.setName("cloth");
        cloth.setSource(materialSource);
        em.persist(cloth);
        Consumer consumer = new Consumer();
        consumer.setAddress(new Address());
        consumer.getAddress().setCity("Springfield");
        consumer.getAddress().setStreet1("4321 Main St");
        consumer.getAddress().setState("OR");
        consumer.getAddress().setZip("12345");
        consumer.setName("Joes Contracting");
        em.persist(consumer.getAddress());
        em.persist(consumer);
        Composite composite = new Composite();
        composite.setHours(4);
        composite.setName("wrapped wood");
        composite.setMaterials(new ArrayList());
        composite.getMaterials().add(wood);
        composite.getMaterials().add(cloth);
        em.persist(composite);
        trans.commit();
        trans.begin();
        em.clear();
        Product productResult = em.find(Product.class, product.getId());
        assertNotNull(productResult);
        assertEquals("product", productResult.getName());
        Worker workerResult = em.find(Worker.class, worker.getId());
        assertNotNull(workerResult);
        assertEquals("role", workerResult.getRole());
        MaterialSource materialSourceResult = em.find(MaterialSource.class, materialSource.getId());
        assertNotNull(materialSourceResult);
        assertEquals("Lowes", materialSourceResult.getName());
        Address addressResult = materialSourceResult.getAddress();
        assertEquals(addressResult.getStreet1(), "1234 Main St");
        assertEquals(addressResult.getCity(), "Springfield");
        assertEquals(addressResult.getState(), "OR");
        assertEquals(addressResult.getZip(), "12345");
        Material woodResult = em.find(Material.class, wood.getId());
        assertNotNull(woodResult);
        assertEquals("wood", woodResult.getName());
        assertEquals(materialSourceResult, woodResult.getSource());
        Material clothResult = em.find(Material.class, cloth.getId());
        assertNotNull(clothResult);
        assertEquals("cloth", clothResult.getName());
        assertEquals(materialSourceResult, woodResult.getSource());
        Consumer consumerResult = em.find(Consumer.class, consumer.getId());
        assertNotNull(consumerResult);
        assertEquals("Joes Contracting", consumerResult.getName());
        addressResult = consumerResult.getAddress();
        assertEquals(addressResult.getStreet1(), "4321 Main St");
        assertEquals(addressResult.getCity(), "Springfield");
        assertEquals(addressResult.getState(), "OR");
        assertEquals(addressResult.getZip(), "12345");
     }

    private static void deleteTestData(EntityManager em) {
        Query q = em.createQuery("delete from Consumer c1 where c1.id like '%'");
        q.executeUpdate();
        q = em.createQuery("delete from Composite c2 where c2.id like '%'");
        q.executeUpdate();
        q = em.createQuery("delete from Material m where m.id like '%'");
        q.executeUpdate();
        q = em.createQuery("delete from MaterialSource ms where ms.id like '%'");
        q.executeUpdate();
        q = em.createQuery("delete from Product p where p.id like '%'");
        q.executeUpdate();
        q = em.createQuery("delete from Worker w where w.id like '%'");
        q.executeUpdate();
        q = em.createQuery("delete from Address a where a.id like '%'");
        q.executeUpdate();
        
    }
    
}
