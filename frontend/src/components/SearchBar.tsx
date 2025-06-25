import React, { useState, useEffect, useRef, useCallback } from 'react';
import {
  Box,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  InputAdornment,
  IconButton,
  Chip,
  Typography
} from '@mui/material';
import {
  Search as SearchIcon,
  Clear as ClearIcon,
  FilterList as FilterIcon
} from '@mui/icons-material';


interface SearchBarProps {
  searchTerm: string;
  onSearchChange: (term: string) => void;
  filterValue: string;
  onFilterChange: (value: string) => void;
  filterOptions: { value: string; label: string }[];
  filterLabel: string;
  placeholder?: string;
  showResultCount?: boolean;
  resultCount?: number;
  totalCount?: number;
}

const SearchBar: React.FC<SearchBarProps> = ({
  searchTerm,
  onSearchChange,
  filterValue,
  onFilterChange,
  filterOptions,
  filterLabel,
  placeholder = "Search...",
  showResultCount = false,
  resultCount = 0,
  totalCount = 0
}) => {
  const [localSearchTerm, setLocalSearchTerm] = useState(searchTerm);
  const timeoutRef = useRef<NodeJS.Timeout | null>(null);

  // Debounced search function
  const debouncedSearch = useCallback((term: string) => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }
    timeoutRef.current = setTimeout(() => {
      onSearchChange(term);
    }, 300);
  }, [onSearchChange]);

  useEffect(() => {
    setLocalSearchTerm(searchTerm);
  }, [searchTerm]);

  // Cleanup timeout on unmount
  useEffect(() => {
    return () => {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
    };
  }, []);

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;
    setLocalSearchTerm(value);
    debouncedSearch(value);
  };

  const handleClearSearch = () => {
    setLocalSearchTerm('');
    onSearchChange('');
  };

  const handleFilterChange = (event: any) => {
    onFilterChange(event.target.value);
  };

  const isSearchActive = localSearchTerm.length > 0 || filterValue !== 'all';

  return (
    <Box sx={{ mb: 3 }}>
      {/* Search and Filter Row */}
      <Box sx={{ display: 'flex', gap: 2, alignItems: 'center', mb: 2 }}>
        {/* Search Input */}
        <TextField
          fullWidth
          variant="outlined"
          placeholder={placeholder}
          value={localSearchTerm}
          onChange={handleSearchChange}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon color="action" />
              </InputAdornment>
            ),
            endAdornment: localSearchTerm && (
              <InputAdornment position="end">
                <IconButton
                  aria-label="clear search"
                  onClick={handleClearSearch}
                  edge="end"
                  size="small"
                >
                  <ClearIcon />
                </IconButton>
              </InputAdornment>
            ),
          }}
          sx={{ flexGrow: 1 }}
        />

        {/* Filter Dropdown */}
        <FormControl sx={{ minWidth: 200 }}>
          <InputLabel id="filter-label">
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <FilterIcon fontSize="small" />
              {filterLabel}
            </Box>
          </InputLabel>
          <Select
            labelId="filter-label"
            value={filterValue}
            onChange={handleFilterChange}
            label={
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <FilterIcon fontSize="small" />
                {filterLabel}
              </Box>
            }
          >
            {filterOptions.map((option) => (
              <MenuItem key={option.value} value={option.value}>
                {option.label}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Box>

      {/* Active Filters and Results */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 1 }}>
        {/* Active Filters */}
        <Box sx={{ display: 'flex', gap: 1, alignItems: 'center', flexWrap: 'wrap' }}>
          {localSearchTerm && (
            <Chip
              label={`Search: "${localSearchTerm}"`}
              onDelete={handleClearSearch}
              color="primary"
              variant="outlined"
              size="small"
            />
          )}
          {filterValue !== 'all' && (
            <Chip
              label={`${filterLabel}: ${filterOptions.find(opt => opt.value === filterValue)?.label}`}
              onDelete={() => onFilterChange('all')}
              color="secondary"
              variant="outlined"
              size="small"
            />
          )}
          {isSearchActive && (
            <Typography variant="body2" color="text.secondary">
              {showResultCount && `${resultCount} of ${totalCount} results`}
            </Typography>
          )}
        </Box>

        {/* Clear All Filters */}
        {isSearchActive && (
          <Box>
            <IconButton
              onClick={() => {
                handleClearSearch();
                onFilterChange('all');
              }}
              size="small"
              color="primary"
              title="Clear all filters"
            >
              <ClearIcon />
            </IconButton>
          </Box>
        )}
      </Box>
    </Box>
  );
};

export default SearchBar;
