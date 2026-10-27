package com.rental.platform.service;

import com.rental.platform.dto.ProductRequest;
import com.rental.platform.exception.ResourceNotFoundException;
import com.rental.platform.exception.UnauthorizedException;
import com.rental.platform.model.Category;
import com.rental.platform.model.Product;
import com.rental.platform.model.User;
import com.rental.platform.repository.CategoryRepository;
import com.rental.platform.repository.ProductRepository;
import com.rental.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    public Page<Product> getOwnerProducts(String ownerEmail, int page, int size) {
        try {
            System.out.println("Finding owner by email: " + ownerEmail);
            User owner = userRepository.findByEmail(ownerEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + ownerEmail));

            System.out.println("Owner found: " + owner.getId() + " - " + owner.getName());
            
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Product> products = productRepository.findByOwnerId(owner.getId(), pageable);
            
            System.out.println("Products found for owner: " + products.getContent().size());
            
            // Force initialization of lazy properties
            products.getContent().forEach(product -> {
                if (product.getCategory() != null) {
                    product.getCategory().getName(); // Force initialization
                }
                if (product.getOwner() != null) {
                    product.getOwner().getName(); // Force initialization
                }
            });
            
            return products;
        } catch (Exception e) {
            System.err.println("Error in getOwnerProducts: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }



    public Product createProduct(ProductRequest request, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPricePerDay(request.getPricePerDay());
        product.setCategory(category);
        product.setOwner(owner);
        product.setImageUrls(request.getImageUrls());
        product.setLocation(request.getLocation());
        product.setLatitude(request.getLatitude());
        product.setLongitude(request.getLongitude());
        product.setStatus(Product.ProductStatus.AVAILABLE);
        product.setApproved(true); // Auto-approve for testing - remove this for production

        return productRepository.save(product);
    }

    public Product updateProduct(Long productId, ProductRequest request, String ownerEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        if (!product.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedException("You can only update your own products");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPricePerDay(request.getPricePerDay());
        product.setCategory(category);
        product.setImageUrls(request.getImageUrls());
        product.setLocation(request.getLocation());
        product.setLatitude(request.getLatitude());
        product.setLongitude(request.getLongitude());
        product.setApproved(false); // Needs re-approval after update

        return productRepository.save(product);
    }

    public Page<Product> getProducts(int page, int size, String sortBy, String sortDir,
            Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
            String keyword, Double lat, Double lon, Double distance) {

			try {
			Sort sort = sortDir.equalsIgnoreCase("desc") ? 
			Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
			Pageable pageable = PageRequest.of(page, size, sort);
			
			System.out.println("ProductService.getProducts - CategoryId: " + categoryId + 
			      ", Keyword: " + keyword + ", Location: " + lat + "," + lon);
			
			// If location-based search is requested
			if (lat != null && lon != null && distance != null) {
			return getProductsNearLocation(lat, lon, distance, pageable);
			}
			
			// Apply category filter first (most specific)
			if (categoryId != null && categoryId > 0) {
			System.out.println("Filtering by category ID: " + categoryId);
			return productRepository.findByApprovedTrueAndStatusAndCategoryId(
			 Product.ProductStatus.AVAILABLE, categoryId, pageable);
			}
			
			// Apply keyword search
			if (keyword != null && !keyword.trim().isEmpty()) {
			System.out.println("Filtering by keyword: " + keyword);
			return productRepository.findByApprovedTrueAndStatusAndNameContainingIgnoreCase(
			 Product.ProductStatus.AVAILABLE, keyword.trim(), pageable);
			}
			
			// Apply price filter
			if (minPrice != null && maxPrice != null && minPrice.compareTo(BigDecimal.ZERO) >= 0) {
			System.out.println("Filtering by price range: " + minPrice + " - " + maxPrice);
			return productRepository.findByApprovedTrueAndStatusAndPricePerDayBetween(
			 Product.ProductStatus.AVAILABLE, minPrice, maxPrice, pageable);
			}
			
			// Default: return all approved and available products
			System.out.println("Returning all approved and available products");
			return productRepository.findByApprovedTrueAndStatus(
			Product.ProductStatus.AVAILABLE, pageable);
			
			} catch (Exception e) {
			System.err.println("Error in getProducts: " + e.getMessage());
			e.printStackTrace();
			
			// Return empty page on error instead of throwing exception
			return Page.empty(PageRequest.of(page, size));
			}
			}

    private Page<Product> getProductsNearLocation(Double lat, Double lon, Double distance, Pageable pageable) {
        try {
            List<Product> nearbyProducts = productRepository.findNearbyProducts(
                    lat, lon, distance, Product.ProductStatus.AVAILABLE.name());
            
            // Convert list to page
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), nearbyProducts.size());
            
            if (start > nearbyProducts.size()) {
                return Page.empty(pageable);
            }
            
            List<Product> pageContent = nearbyProducts.subList(start, end);
            return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, nearbyProducts.size());
        } catch (Exception e) {
            // If location query fails, return regular products
            return productRepository.findByApprovedTrueAndStatus(Product.ProductStatus.AVAILABLE, pageable);
        }
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public Product updateProductStatus(Long productId, Product.ProductStatus status, String ownerEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        if (!product.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedException("You can only update your own products");
        }

        product.setStatus(status);
        return productRepository.save(product);
    }

    public void deleteProduct(Long productId, String ownerEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        if (!product.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedException("You can only delete your own products");
        }

        productRepository.delete(product);
    }

    // Admin methods
    public Product approveProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setApproved(true);
        notificationService.notifyProductApproval(product, true);
        return productRepository.save(product);

    }

    public Product rejectProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setApproved(false);
        product.setStatus(Product.ProductStatus.UNAVAILABLE);
        notificationService.notifyProductApproval(product, false);
        return productRepository.save(product);

    }

    public Page<Product> getPendingProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        return productRepository.findByApprovedFalse(pageable);
    }
}